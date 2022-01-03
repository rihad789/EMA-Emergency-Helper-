package com.service.ema.Fragment_Helpline_Action;

public class Helpline_POJO_Class {

    String helpline_contacts_name,helpline_contacts_no,helpline_service_area;

    public Helpline_POJO_Class() {
    }

    public Helpline_POJO_Class(String helpline_contacts_name, String helpline_contacts_no, String helpline_service_area) {
        this.helpline_contacts_name = helpline_contacts_name;
        this.helpline_contacts_no = helpline_contacts_no;
        this.helpline_service_area = helpline_service_area;
    }

    public String getHelpline_contacts_name() {
        return helpline_contacts_name;
    }

    public void setHelpline_contacts_name(String helpline_contacts_name) {
        this.helpline_contacts_name = helpline_contacts_name;
    }

    public String getHelpline_contacts_no() {
        return helpline_contacts_no;
    }

    public void setHelpline_contacts_no(String helpline_contacts_no) {
        this.helpline_contacts_no = helpline_contacts_no;
    }

    public String getHelpline_service_area() {
        return helpline_service_area;
    }

    public void setHelpline_service_area(String helpline_service_area) {
        this.helpline_service_area = helpline_service_area;
    }
}
