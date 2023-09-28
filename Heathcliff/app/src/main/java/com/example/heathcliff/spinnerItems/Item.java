package com.example.heathcliff.spinnerItems;

public class Item {
   private String name;
   private Boolean checked;

   public Item (String name, Boolean checked ){
       this.name = name;
       this.checked = checked;
   }
   public Item(){

   }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
