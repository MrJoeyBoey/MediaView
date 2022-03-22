package com.j.mediaview.utils;

import com.j.mediaview.beans.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckArray<T> extends ArrayList<Check<T>> {

    public CheckArray() {
    }

    public void singleCheck(int checkPosition) {
        singleCheck(this, checkPosition);
    }

    public static <T> void singleCheck(List<Check<T>> checkList, int checkPosition) {
        if (checkList != null && checkPosition >= 0 && checkPosition < checkList.size()) {
            Check<T> check = checkList.get(checkPosition);
            singleCheck(checkList, check.getData());
        }
    }
    public static <T> void singleCheck(List<Check<T>> checkList, T item) {
        if (checkList != null) {
            for (Check<T> check : checkList){
                check.setChecked(check.getData().equals(item));
            }
        }
    }

    public static <T> int getCheckPosition(List<Check<T>> checkList){
        if (checkList != null) {
            for (Check<T> check : checkList) {
                if (check.isChecked()) return checkList.indexOf(check);
            }
        }
        return -1;
    }

    public static <T> T getCheckItem(List<Check<T>> checkList){
        List<T> ts = getCheckItems(checkList);
        return ts.size() > 0 ? ts.get(0) : null;
    }
    public static <T> List<T> getCheckItems(List<Check<T>> checkList){
        List<T> ts = new ArrayList<>();
        if (checkList != null) {
            for (Check<T> check : checkList) {
                if (check.isChecked()) ts.add(check.getData());
            }
        }
        return ts;
    }

    @SafeVarargs
    public static <T> CheckArray<T> asCheckArray(T... list){
        return asCheckArray(Arrays.asList(list), -1);
    }
    public static <T> CheckArray<T> asCheckArray(List<T> list){
        return asCheckArray(list,-1);
    }
    public static <T> CheckArray<T> asCheckArray(List<T> list, int checkPosition){
        CheckArray<T> checkArray = new CheckArray<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i ++){
                T t = list.get(i);
                checkArray.add(new Check<>(t,i == checkPosition));
            }
        }
        return checkArray;
    }

}
