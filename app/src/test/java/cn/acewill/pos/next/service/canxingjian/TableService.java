package cn.acewill.pos.next.service.canxingjian;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import cn.acewill.pos.next.service.canxingjian.retrofit.message.AllTableOrders;

import static org.junit.Assert.assertEquals;

/**
 * Created by Acewill on 2016/6/3.
 */
@Ignore
public class TableService {

    //安卓里没有JDK7中的Paths, Files等工具类
    public static String readFileAsString(String filePath) throws URISyntaxException {

        String result = "";
        File file = new File(filePath);
        if ( file.exists() ) {
            FileInputStream fis = null;
            try {

                fis = new FileInputStream(file);
                char current;
                while (fis.available() > 0) {
                    current = (char) fis.read();
                    result = result + String.valueOf(current);

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException ignored) {
                    }
            }
        }
        return result;
    }


    @Test
    public void getTablesStatus() throws URISyntaxException {
        URL url = ClassLoader.getSystemResource("canxingjian/table/getTablesStatus.json");
        String expectedProducts =  readFileAsString(url.getPath());


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<String> tableList = gson.fromJson(expectedProducts, List.class);
        assertEquals(20, tableList.size());
    }

    @Test
    public void getTableOrders() throws URISyntaxException {
        URL url = ClassLoader.getSystemResource("canxingjian/table/getAllOrders.json");
        String expectedProducts =  readFileAsString(url.getPath());


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        AllTableOrders orderList = gson.fromJson(expectedProducts, AllTableOrders.class);
        assertEquals(20, orderList.orders.size());
    }
    @Test
    public void getIsSqlListUpdata() throws URISyntaxException {
        URL url = ClassLoader.getSystemResource("canxingjian/table/isSqliteUpdata.json");
        String expectedProducts =  readFileAsString(url.getPath());
        boolean isUpdata = Boolean.valueOf(expectedProducts);
    }
}
