package api;

import exp.Exps;
import service.BasketFinder_by_stocks;
import stocksHandler.StocksHandler;

public abstract class INDEX_OBJECT extends BASE_CLIENT_OBJECT {

    protected abstract void init_baskets_service();
    protected abstract void init_stocks_handler();
    protected abstract void init_index_delta_service();
    protected abstract void init_exps();
    protected Exps exps;

    protected double index;

    public INDEX_OBJECT() {
        super();
        init_stocks_handler();
        init_baskets_service();
    }

    private String status = null;
    private String streamMarket = "stream";

    private double interest = 0.006;
    private boolean dbLoaded = false;
    private double rando = 0;

    public BasketFinder_by_stocks basketFinder_by_stocks;
    private StocksHandler stocksHandler;

    public Exps getExps() {
        return exps;
    }
    public void setExps(Exps exps) {
        this.exps = exps;
    }
    public StocksHandler getStocksHandler() {
        return stocksHandler;
    }
    public void setStocksHandler(StocksHandler stocksHandler) {
        this.stocksHandler = stocksHandler;
    }

    public BasketFinder_by_stocks getBasketFinder_by_stocks() {
        return basketFinder_by_stocks;
    }
    public void setBasketFinder_by_stocks(BasketFinder_by_stocks basketFinder_by_stocks) {
        this.basketFinder_by_stocks = basketFinder_by_stocks;
    }
    public double getIndex() {
        return index;
    }
    public void setIndex(double index) {
        this.index = index;
    }
}
