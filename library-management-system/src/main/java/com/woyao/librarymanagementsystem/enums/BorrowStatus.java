package com.woyao.librarymanagementsystem.enums;

public enum BorrowStatus {
    BORROWED(0),
    RETURNED(1);

    private int code;

    BorrowStatus(int code) {
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
