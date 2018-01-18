/* 
* @File: TableViewController.java
* @Author: Abhi Gupta
* 
* This class contains constants used by all classes that have a table view
*/

package sample;

public interface TableViewController {
    int OFFSET = 52;
    int CELL_HEIGHT = 70;
    int CELL_WIDTH = 270;
    int TABLE_WIDTH = CELL_WIDTH;
    int TABLE_HEIGHT = ViewController.SCREEN_HEIGHT-OFFSET;
}
