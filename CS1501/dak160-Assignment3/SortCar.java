/**
 *
 * Daniel Kindler
 * CS1501 Project 3
 * dak160@pitt.edu
 *
 **/

public class SortCar implements Comparable<SortCar> {
    int sortValue;
    int index;
    String vin;

    public SortCar(int i, int sortV){
        this.sortValue = sortV;
        this.index = i;
    }

    /**
     * Returns -1 if this.sortValue is less than c.sortValue
     * Returns 0 if this.sortValue == c.sortValue
     * Returns 1 if this.sortValue is greater than c.sortValue
     */
    @Override
    public int compareTo(SortCar c){
        if (this.sortValue < c.sortValue) {
            return -1;
        } else if (this.sortValue == c.sortValue) {
            return 0;
        } else {
            return 1;
        }
    }
}
