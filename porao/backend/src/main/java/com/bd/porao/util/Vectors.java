package com.bd.porao.util;

public class Vectors
{
    private Vectors()
    {

    }
    public static double cosine(float[]a, float[]b)
    {
        double dot = 0;
        double na = 0;
        double nb = 0;
        for (int i = 0; i < a.length && i < b.length; i++)
        {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb)+1e-9);
    }
}
