package com.thyb.weightservice;

import Weight;
 interface IWeight {
    Weight getWeight() ;
    boolean setTare(); //ȥƤ
    boolean setZero(); //����
    void doNumberTare(int numbertare);//����ȥƤ
    boolean reset();  //��λ��
}
