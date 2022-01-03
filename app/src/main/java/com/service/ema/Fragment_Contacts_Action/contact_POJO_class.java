package com.service.ema.Fragment_Contacts_Action;

public class contact_POJO_class {
    String ID,Name,Phone;

    public contact_POJO_class() {
    }

    public contact_POJO_class(String ID, String name, String phone) {
        this.ID = ID;
        Name = name;
        Phone = phone;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
