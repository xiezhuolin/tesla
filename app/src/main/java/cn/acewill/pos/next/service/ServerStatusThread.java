package cn.acewill.pos.next.service;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketTimeoutException;

import cn.acewill.pos.next.dao.SystemVariablesDao;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.SystemVariables;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.utils.Constant;

/**
 * Created by Acewill on 2016/8/15.
 */
public class ServerStatusThread extends Thread {

    public void run() {

        SystemService systemService = null;
        SystemVariablesDao systemVariablesDao = new SystemVariablesDao();

        SystemVariables serverUrlVariable = systemVariablesDao.getValue("server.url");
        String serverUrl = null;
        if (serverUrlVariable != null) {
            serverUrl = serverUrlVariable.getValue();
            Log.d("ServerStatusThread", "server url in db: " + serverUrl);
        }

        PosInfo posInfo = PosInfo.getInstance();
        if (posInfo.getServerUrl() == null || posInfo.getServerUrl().isEmpty()) {
            if (serverUrl != null) {
                posInfo.setServerUrl(serverUrl);
            }
        }

        try {
            systemService = SystemService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                systemService.isServerRunning(new ResultCallback() {
                    @Override
                    public void onResult(Object result) {
                        ServerStatus.getInstance().setRunning(true);
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        ServerStatus.getInstance().setRunning(false);
                    }
                });
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                Thread.sleep(10 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                if(e.getCause() instanceof SocketTimeoutException)
                {
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SERVER_REQUEST_TIMEOUT));
                }
            }
        }
    }
}
