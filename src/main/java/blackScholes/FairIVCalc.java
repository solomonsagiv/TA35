package blackScholes;

import options.Options;
import options.Strike;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class FairIVCalc {

    // ==== מבנה נתון לתוצאה לכל סטרייק ====
    public static class StrikeResult {
        public final double strike;
        public final double marketIV;      // IV מהשוק (annualized, decimal)
        public final double fairIV;        // IV הוגן כולל סקיו (annualized, decimal)
        public final double mispricing;    // (marketIV / fairIV - 1)

        public StrikeResult(double strike, double marketIV, double fairIV, double mispricing) {
            this.strike = strike;
            this.marketIV = marketIV;
            this.fairIV = fairIV;
            this.mispricing = mispricing;
        }

        @Override
        public String toString() {
            return String.format("Strike=%.2f, marketIV=%.4f, fairIV=%.4f, mispricing=%.2f%%",
                    strike, marketIV, fairIV, mispricing * 100.0);
        }
    }

    // ==== פונקציה ראשית: בונה את המודל לכל הסטרייקים ====
    /**
     * @param closes     מחירי סגירה יומיים של המדד (רק ימי מסחר), מהישן לחדש. צריך לפחות 21–22 ימים.
     * @param dteDays    Days To Expiry (ימים לפקיעה), כמו D1 באקסל
     * @param spot       מחיר המדד/חוזה (מקביל ל-F1 באקסל)
     * @param strikes    מערך סטרייקים
     * @param marketIVs  מערך IV מהשוק לכל סטרייק (annualized, decimal; למשל 0.22 = 22%)
     * @return רשימת StrikeResult עם fairIV + mispricing לכל סטרייק
     */
    public static List<StrikeResult> buildSurface(
            double[] closes,
            double dteDays,
            double spot,
            double[] strikes,
            double[] marketIVs
    ) {
        if (closes == null || closes.length < 21) {
            throw new IllegalArgumentException("צריך לפחות ~21 מחירי סגירה לחישוב HV10/HV20");
        }
        if (strikes.length != marketIVs.length) {
            throw new IllegalArgumentException("אורך strikes חייב להיות זהה ל-marketIVs");
        }

        // 1) תשואות לוג יומיות
        double[] logReturns = calcLogReturns(closes);

        // 2) HV10 / HV20 יומיים ושנתיים (252 ימי מסחר בשנה)
        double hv10Daily = stdDevLastN(logReturns, 10);
        double hv20Daily = stdDevLastN(logReturns, 20);

        double hv10Annual = hv10Daily * Math.sqrt(252.0);
        double hv20Annual = hv20Daily * Math.sqrt(252.0);

        // 3) HV_eff לפי DTE (כמו C23 באקסל)
        double hvEff = blendHV(hv10Annual, hv20Annual, dteDays);

        // 4) פקטורי סקיו בסיסיים (אפשר להתאים למדד שלך)
        double pSkewBase = 0.8;  // M1 באקסל
        double cSkewBase = 0.3;  // M2 באקסל

        // 5) פקטור סקיו לפי ימים לפקיעה (M3 / M4)
        double pSkewEff = computePutSkewEff(pSkewBase, dteDays);
        double cSkewEff = computeCallSkewEff(cSkewBase, dteDays);

        // 6) פקטור "רג'ים" לפי רמת הסטייה הכוללת (M5, תלוי hvEff)
        double volRegimeFactor = computeVolRegimeFactor(hvEff);

        // 7) חישוב fairIV + mispricing לכל סטרייק
        List<StrikeResult> results = new ArrayList<>();
        for (int i = 0; i < strikes.length; i++) {
            double k = strikes[i];
            double ivMarket = marketIVs[i];

            double moneyness = k / spot - 1.0;  // כמו I = E/Spot - 1

            double fairIV = computeFairIVWithSkew(
                    hvEff,
                    moneyness,
                    pSkewEff,
                    cSkewEff,
                    volRegimeFactor
            );

            double mispricing = (fairIV != 0.0) ? (ivMarket / fairIV - 1.0) : 0.0;

            results.add(new StrikeResult(k, ivMarket, fairIV, mispricing));
        }

        return results;
    }

    // ==== עזר: חישוב תשואות לוג ====
    private static double[] calcLogReturns(double[] closes) {
        double[] r = new double[closes.length - 1];
        for (int i = 1; i < closes.length; i++) {
            r[i - 1] = Math.log(closes[i] / closes[i - 1]);
        }
        return r;
    }

    // ==== עזר: סטיית תקן מדגמית של N התשואות האחרונות ====
    private static double stdDevLastN(double[] data, int n) {
        if (data.length < n) {
            throw new IllegalArgumentException("אין מספיק נתונים עבור " + n + " ימים");
        }
        int start = data.length - n;
        double mean = 0.0;
        for (int i = start; i < data.length; i++) {
            mean += data[i];
        }
        mean /= n;

        double sumSq = 0.0;
        for (int i = start; i < data.length; i++) {
            double diff = data[i] - mean;
            sumSq += diff * diff;
        }
        // STDEV.S = sqrt( Σ (x-mean)^2 / (n-1) )
        return Math.sqrt(sumSq / (n - 1));
    }

    // ==== HV_eff לפי DTE (כמו IFים ב-C23) ====
    private static double blendHV(double hv10Annual, double hv20Annual, double dteDays) {
        if (dteDays <= 12.0) {
            return 0.7 * hv10Annual + 0.3 * hv20Annual;
        } else if (dteDays <= 20.0) {
            return 0.6 * hv10Annual + 0.4 * hv20Annual;
        } else if (dteDays <= 35.0) {
            return 0.5 * hv10Annual + 0.5 * hv20Annual;
        } else {
            return 0.4 * hv10Annual + 0.6 * hv20Annual;
        }
    }

    // ==== Put skew eff לפי ימים (M3 באקסל) ====
    private static double computePutSkewEff(double pSkewBase, double dteDays) {
        if (dteDays <= 12.0) {
            return pSkewBase * 1.3;
        } else if (dteDays <= 20.0) {
            return pSkewBase * 1.1;
        } else if (dteDays <= 35.0) {
            return pSkewBase;
        } else {
            return pSkewBase * 0.9;
        }
    }

    // ==== Call skew eff לפי ימים (M4 באקסל) ====
    private static double computeCallSkewEff(double cSkewBase, double dteDays) {
        if (dteDays <= 12.0) {
            return cSkewBase * 1.2;
        } else if (dteDays <= 20.0) {
            return cSkewBase * 1.05;
        } else if (dteDays <= 35.0) {
            return cSkewBase;
        } else {
            return cSkewBase * 0.9;
        }
    }

    // ==== Vol regime factor לפי hvEff (M5 באקסל) ====
    private static double computeVolRegimeFactor(double hvEff) {
        if (hvEff < 0.15) {
            return 0.8;   // שוק שקט – סקיו מוחלש
        } else if (hvEff < 0.25) {
            return 1.0;   // רגיל
        } else {
            return 1.2;   // שוק לחוץ – סקיו מוגבר
        }
    }

    // ==== חישוב IV הוגן כולל סקיו לכל סטרייק (כמו J בעמודה) ====
    private static double computeFairIVWithSkew(
            double hvEff,
            double moneyness,        // (K/Spot - 1)
            double pSkewEff,         // M3
            double cSkewEff,         // M4
            double volRegimeFactor   // M5
    ) {
        double ivFair = hvEff;

        if (moneyness < 0.0) {
            // פוט מתחת לכסף
            double x = Math.abs(moneyness * 100.0);
            ivFair += pSkewEff * volRegimeFactor * Math.pow(x, 1.1) / 100.0;
        } else if (moneyness > 0.0) {
            // קול מעל הכסף
            double x = moneyness * 100.0;
            ivFair += -cSkewEff * volRegimeFactor * Math.pow(x, 1.1) / 100.0;
        }

        return ivFair;
    }

    // ==== (בונוס) חישוב IV של CALL לפי מחיר – ניוטון–רפסון ====
    /**
     * מחזיר IV של קול (annualized, decimal) לפי:
     * spot, strike, rate, ימים לפקיעה, מחיר שוק, וניחוש התחלתי ל-IV
     */
    public static double impliedVolCall(
            double spot,
            double strike,
            double rate,
            double dteDays,
            double callPrice,
            double ivInitial
    ) {
        double T = dteDays / 252.0;
        double iv = ivInitial;
        if (T <= 0 || spot <= 0 || strike <= 0) {
            throw new IllegalArgumentException("קלטים לא תקינים ל-IV");
        }

        // עד 20 איטרציות, או עצירה כשהשגיאה קטנה
        for (int iter = 0; iter < 20; iter++) {
            double d1 = (Math.log(spot / strike) + (rate + 0.5 * iv * iv) * T) / (iv * Math.sqrt(T));
            double d2 = d1 - iv * Math.sqrt(T);

            double theo = spot * normCdf(d1) - strike * Math.exp(-rate * T) * normCdf(d2);
            double vega = spot * normPdf(d1) * Math.sqrt(T);

            double diff = theo - callPrice; // כמה אנחנו רחוקים ממחיר השוק

            if (Math.abs(diff) < 1e-6) {
                break;
            }
            if (vega < 1e-8) {
                break; // להימנע מחלוקה באפס
            }

            iv = iv - diff / vega; // ניוטון–רפסון

            // לשמור על תחום סביר
            if (iv <= 0.0001) iv = 0.0001;
            if (iv > 5.0) iv = 5.0;
        }

        return iv;
    }

    // ==== פונקציות נורמל סטנדרטי (CDF + PDF) ====
    private static double normPdf(double x) {
        return (1.0 / Math.sqrt(2.0 * Math.PI)) * Math.exp(-0.5 * x * x);
    }

    // CDF מקורב לגאוס
    private static double normCdf(double x) {
        // Approximation by Abramowitz and Stegun
        double sign = (x < 0) ? -1.0 : 1.0;
        x = Math.abs(x) / Math.sqrt(2.0);

        double t = 1.0 / (1.0 + 0.3275911 * x);
        double a1 = 0.254829592;
        double a2 = -0.284496736;
        double a3 = 1.421413741;
        double a4 = -1.453152027;
        double a5 = 1.061405429;

        double erf = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        return 0.5 * (1.0 + sign * erf);
    }

    // ==== פונקציה מותאמת ל-Options (month) ====
    /**
     * מחשב fair IV לכל הסטרייקים ב-Options (month) על בסיס מחירי סגירה היסטוריים.
     * 
     * @param optionsMonth  אובייקט Options של month options
     * @param closes        מחירי סגירה יומיים של המדד/חוזה (רק ימי מסחר), מהישן לחדש. צריך לפחות 21–22 ימים.
     * @return רשימת StrikeResult עם fairIV + mispricing לכל סטרייק, ממוינת לפי strike
     */
    public static List<StrikeResult> calculateFairIVForOptions(Options optionsMonth, double[] closes) {
        if (optionsMonth == null) {
            throw new IllegalArgumentException("Options object cannot be null");
        }
        if (closes == null || closes.length < 21) {
            throw new IllegalArgumentException("צריך לפחות ~21 מחירי סגירה לחישוב HV10/HV20");
        }

        // קבלת נתונים מ-Options
        double spot = optionsMonth.getContract();
        double dteDays = optionsMonth.getDays_to_exp();

        if (spot <= 0) {
            throw new IllegalArgumentException("Contract price must be positive");
        }
        if (dteDays <= 0) {
            throw new IllegalArgumentException("Days to expiry must be positive");
        }

        // איסוף strikes ו-market IVs
        List<Strike> strikesList = new ArrayList<>(optionsMonth.getStrikes());
        strikesList.sort(Comparator.comparingDouble(Strike::getStrike));

        if (strikesList.isEmpty()) {
            return new ArrayList<>();
        }

        double[] strikes = new double[strikesList.size()];
        double[] marketIVs = new double[strikesList.size()];

        for (int i = 0; i < strikesList.size(); i++) {
            Strike strike = strikesList.get(i);
            strikes[i] = strike.getStrike();

            // בחירת IV מהאופציה המתאימה: אם strike > spot, לוקחים call IV, אחרת put IV
            // אם אין IV, נסה לקחת מה-strike עצמו, אחרת 0
            double marketIV = 0.0;
            if (strike.getStrike() > spot) {
                // OTM Call - לוקחים call IV
                if (strike.getCall() != null && strike.getCall().getIv() > 0) {
                    marketIV = strike.getCall().getIv();
                } else if (strike.getIv() > 0) {
                    marketIV = strike.getIv();
                }
            } else {
                // OTM Put - לוקחים put IV
                if (strike.getPut() != null && strike.getPut().getIv() > 0) {
                    marketIV = strike.getPut().getIv();
                } else if (strike.getIv() > 0) {
                    marketIV = strike.getIv();
                }
            }

            // אם עדיין אין IV, נסה לקחת מהאופציה השנייה
            if (marketIV <= 0) {
                if (strike.getCall() != null && strike.getCall().getIv() > 0) {
                    marketIV = strike.getCall().getIv();
                } else if (strike.getPut() != null && strike.getPut().getIv() > 0) {
                    marketIV = strike.getPut().getIv();
                } else if (strike.getIv() > 0) {
                    marketIV = strike.getIv();
                }
            }

            marketIVs[i] = marketIV; // IV כבר ב-decimal (annualized)
        }

        // קריאה לפונקציה הראשית
        return buildSurface(closes, dteDays, spot, strikes, marketIVs);
    }

    /**
     * מחשב fair IV ומעדכן את ה-Strike objects עם fair IV.
     * 
     * @param optionsMonth  אובייקט Options של month options
     * @param closes        מחירי סגירה יומיים של המדד/חוזה
     * @return רשימת StrikeResult עם fairIV + mispricing לכל סטרייק
     */
    public static List<StrikeResult> calculateAndUpdateFairIV(Options optionsMonth, double[] closes) {
        List<StrikeResult> results = calculateFairIVForOptions(optionsMonth, closes);
        
        // עדכון Strike objects עם fair IV
        for (StrikeResult result : results) {
            Strike strike = optionsMonth.getStrike(result.strike);
            if (strike != null) {
                strike.setFairIv(result.fairIV);
            }
        }
        
        return results;
    }

    // ==== דוגמה לשימוש ====
    public static void main(String[] args) {
        // דוגמה: 22 סגירות (פה סתם מספרים – אתה תכניס נתוני אמת)
        double[] closes = {1900,1910,1920,1915,1925,1930,1940,1935,1945,1950,
                           1960,1970,1965,1975,1980,1990,2000,1995,2005,2010,2020,2030};

        double dteDays = 19;      // כמו D1
        double spot    = 2030;    // כמו F1
        double[] strikes   = {1950, 2000, 2050, 2100};
        double[] marketIVs = {0.21, 0.205, 0.215, 0.23};

        List<StrikeResult> res = buildSurface(closes, dteDays, spot, strikes, marketIVs);
        for (StrikeResult r : res) {
            System.out.println(r);
        }
    }
}
