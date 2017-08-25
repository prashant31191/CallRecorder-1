package com.test.drinkwaterdemo.model;

import java.util.ArrayList;

/**
 * Created by Avinash Kahal on 04-Jul-17.
 */

public class Group {

    //private String quantityGroup;

    public String groupQuantityML;

    private String dateGroup;

    public String getDateGroup() {
        return dateGroup;
    }

    public void setDateGroup(String dateGroup) {
        this.dateGroup = dateGroup;
    }

    private ArrayList<ModelDrinkList> childItem;

   /* public String getQuantityGroup() {
        return quantityGroup;
    }

    public void setQuantityGroup(String quantityGroup) {
        this.quantityGroup = quantityGroup;
    }*/

    public String getGroupQuantityML() {
        return groupQuantityML;
    }

    public void setGroupQuantityML(String groupQuantityML) {
        this.groupQuantityML = groupQuantityML;
    }

    public ArrayList<ModelDrinkList> getChildItem() {
        return childItem;
    }

    public void setChildItem(ArrayList<ModelDrinkList> childItem) {
        this.childItem = childItem;
    }
}
