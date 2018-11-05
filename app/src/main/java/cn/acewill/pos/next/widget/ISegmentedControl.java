package cn.acewill.pos.next.widget;

/**
 * Created by song on 2016/10/12.
 *
 */

public interface ISegmentedControl {

    int getCount();

    SegmentedControlItem getItem(int position);

    String getName(int position);

}
