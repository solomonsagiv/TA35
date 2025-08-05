package miniStocks;

// Cells
public class MiniStockDDECells {

    // Variables
    private String lastPriceCell = "R%sC12";
    private String volumeCell = "R%sC13";
    private String nameCell = "R%sC17";
    private String bidCell = "R%sC14";
    private String askCell = "R%sC15";
    private String weightCell = "R%sC16";
    private String baseCell = "R%sC19";
    private String openCell = "R%sC18";
//    private String bidSizeCell = "R%C";
//    private String askSizeCell = "R%C";
    private int row;

    // Constructor
    public MiniStockDDECells(int row) {
        this.row = row;
        this.lastPriceCell = String.format(lastPriceCell, row);
        this.volumeCell = String.format(volumeCell, row);
        this.nameCell = String.format(nameCell, row);
        this.bidCell = String.format(bidCell, row);
        this.askCell = String.format(askCell, row);
        this.weightCell = String.format(weightCell, row);
//        this.bidSizeCell = String.format(bidSizeCell, row);
//        this.askSizeCell = String.format(askSizeCell, row);
    }

    // Getters and Setters
    public String getLastPriceCell() {
        return lastPriceCell;
    }

    public String getBidCell() {
        return bidCell;
    }

    public String getAskCell() {
        return askCell;
    }

    public void setLastPriceCell(String lastPriceCell) {
        this.lastPriceCell = lastPriceCell;
    }

    public String getVolumeCell() {
        return volumeCell;
    }

    public void setVolumeCell(String volumeCell) {
        this.volumeCell = volumeCell;
    }

    public String getNameCell() {
        return nameCell;
    }

    public void setNameCell(String nameCell) {
        this.nameCell = nameCell;
    }

    public int getRow() {
        return row;
    }

    public String getWeightCell() {
        return weightCell;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getBaseCell() {
        return baseCell;
    }

    public String getOpenCell() {
        return openCell;
    }

    //    public String getBidSizeCell() {
//        return bidSizeCell;
//    }
//
//    public void setBidSizeCell(String bidSizeCell) {
//        this.bidSizeCell = bidSizeCell;
//    }
//
///    public String getAskSizeCell() {
//        return askSizeCell;
//    }
//
//    public void setAskSizeCell(String askSizeCell) {
//        this.askSizeCell = askSizeCell;
//    }
}