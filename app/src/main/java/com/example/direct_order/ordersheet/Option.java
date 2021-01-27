package com.example.direct_order.ordersheet;

import java.util.ArrayList;

public class Option extends SubOption {
    public ArrayList<SubOption> subOptionArrayList;
    OptionForm optionForm;

    public Option(int number, String title, SubOptionForm subOptionForm) {
        super(number, title, subOptionForm);
        optionForm = (OptionForm) subOptionForm;
    }

    public OptionForm getOptionForm() {
        return optionForm;
    }

    public void setOptionForm(OptionForm optionForm) {
        this.optionForm = optionForm;
    }
}
