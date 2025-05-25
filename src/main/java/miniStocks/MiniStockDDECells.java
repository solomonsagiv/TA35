package miniStocks;

// Cells
public class MiniStockDDECells {

    // Variables
    private String lastPriceCell = "R%sC5";
    private String volumeCell = "R%sC6";
    private String nameCell = "R%sC10";
    private String bidCell = "R%sC7";
    private String askCell = "R%sC8";
    private String weightCell = "R%sC9";
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
}