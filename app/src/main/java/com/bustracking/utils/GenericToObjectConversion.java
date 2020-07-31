package com.bustracking.utils;

import com.bustracking.model.busmodels.BusLine;

import java.util.ArrayList;

public class GenericToObjectConversion
{
    public static Object[] convertBusLineInstanceToObject(ArrayList<BusLine> classTypeArray)
    {
        try
        {
            Object[] objects = new Object[classTypeArray.size()];

            for(int i = 0; i < classTypeArray.size(); i++)
            {
                objects[i] = (Object)classTypeArray.get(i);
            }

            return objects;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
