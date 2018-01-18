/* 
* @File: MessagesViewController.java
* @Author: Abhi Gupta
* 
* Any class that renders messages on to a table view will use these constants
*/
package sample;

public interface MessagesViewController {
    int BAR_WIDTH = ViewController.SCREEN_WIDTH-TableViewController.CELL_WIDTH;
    int BAR_HEIGHT = 40;
    int BAR_X = ViewController.SCREEN_WIDTH-BAR_WIDTH;
    int BAR_GATEWAY_WIDTH = ViewController.SCREEN_WIDTH;
    int BAR_Y = ViewController.SCREEN_HEIGHT-BAR_HEIGHT;
    int MESSAGE_BAR_WIDTH = 424;
    int MESSAGE_BAR_GATEWAY_WIDTH = ViewController.SCREEN_WIDTH-20;
    double BUBBLE_MAX_WIDTH = ViewController.SCREEN_WIDTH/1.5;
    int VIEW_WIDTH = ViewController.SCREEN_WIDTH-TableViewController.CELL_WIDTH;
    int VIEW_POS_X = TableViewController.CELL_WIDTH;
    int VIEW_MAX_HEIGHT = ViewController.SCREEN_HEIGHT-TableViewController.OFFSET-BAR_HEIGHT;
    int VIEW_POS_Y = TableViewController.OFFSET;
    double VIEW_MAX_WIDTH = VIEW_WIDTH/1.5;
}
