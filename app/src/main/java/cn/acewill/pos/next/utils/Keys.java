package cn.acewill.pos.next.utils;

/**
 * Created by aqw on 2016/8/19.
 */
public enum  Keys {
    KEY0(0),
    KEY1(1),
    KEY2(2),
    KEY3(3),
    KEY4(4),
    KEY5(5),
    KEY6(6),
    KEY7(7),
    KEY8(8),
    KEY9(9),
    KEYPOINT(10),
    KEYDE(11),
    KEYCL(12),
    KEYPAY(13),
    KEYOK(14),
    KEYMONEY_ONE(15),
    KEYMONEY_TWO(16),
    KEYMONEY_THREE(17),

    KEYTen(110),
    KEY20(20),
    KEY50(50),
    KEY100(100);

    private int value = 0;

    private Keys(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

}
