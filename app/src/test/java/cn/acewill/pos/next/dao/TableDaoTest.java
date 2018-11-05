package cn.acewill.pos.next.dao;

import android.os.Build;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.List;

import cn.acewill.pos.BuildConfig;
import cn.acewill.pos.next.model.table.Table;

import static org.junit.Assert.assertEquals;


@RunWith(RobolectricGradleTestRunner.class) //不要用RobolectricTestRunner，否则会提示manifest文件找不到
//需要制定sdk版本，因为不要用Robolectric不支持23
@Config(constants = BuildConfig.class,sdk = Build.VERSION_CODES.LOLLIPOP, shadows = {ShadowContentResolver.class})
public class TableDaoTest {
    TableDao tableDao;
    @Before
    public void setUp() {
        FlowManager.init(new FlowConfig.Builder(RuntimeEnvironment.application).build());
        tableDao = new TableDao();
    }

    @Test
    public void saveAndGetTables() {
        Table t = new Table();
        t.setName( "Wiki");
        t.setCapacity(1);
//        t.setBookingStatus(TableStatus.BookingStatus.IN_USE);
//        t.setStatus(TableStatus.UseStatus.DIRTY);

        tableDao.saveTable(t);

        List<Table> tables = tableDao.getAllTables();
        assertEquals(1, tables.size());

        Table tableFromDB = tables.get(0);
        assertEquals("Wiki", tableFromDB.getName());
        assertEquals(1, tableFromDB.getCapacity());
//        assertEquals(TableStatus.UseStatus.DIRTY, tableFromDB.getStatus());
//        assertEquals(TableStatus.BookingStatus.IN_USE, tableFromDB.getBookingStatus());
    }
}