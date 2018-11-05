package cn.acewill.pos.next.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.KDS;
import cn.acewill.pos.next.model.KitchenStall;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.ui.activity.newPos.KDSAty;
import cn.acewill.pos.next.ui.activity.newPos.PrinterInfoAty;
import cn.acewill.pos.next.ui.adapter.KDSAdp;
import cn.acewill.pos.next.ui.adapter.KichenStallsAdp;
import cn.acewill.pos.next.ui.adapter.PrintsAdp;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class PrintKDSFragment extends BaseFragment {
    @BindView( R.id.lv_printer )
    ScrolListView lvPrinter;
    @BindView( R.id.lv_kds )
    ScrolListView lvKDS;
    @BindView( R.id.lv_printerPlan )
    ScrolListView lvPrinterPlan;
    @BindView( R.id.iv_add_printer )
    ImageView ivAddPrinter;
    @BindView( R.id.img_add_kds )
    ImageView ivAddKds;

    private PrintsAdp printsAdp;
    private KDSAdp kdsAdp;
    private KichenStallsAdp kichenStallsAdp;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print_kds, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        intent = new Intent();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPrinterList();
    }

    private void setData() {
        printsAdp = new PrintsAdp(aty);
        printsAdp.setData(PrinterDataController.getPrinterList());
        lvPrinter.setAdapter(printsAdp);

        kdsAdp = new KDSAdp(aty);
        kdsAdp.setData(PrinterDataController.getKdsList());
        lvKDS.setAdapter(kdsAdp);

        kichenStallsAdp = new KichenStallsAdp(aty);
        kichenStallsAdp.setData(PrinterDataController.getKitchenStallList());
        lvPrinterPlan.setAdapter(kichenStallsAdp);

        lvPrinter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Printer printer = (Printer) printsAdp.getItem(position);
                if (printer != null) {
                    intent.setClass(aty, PrinterInfoAty.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("printer", printer);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        lvKDS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KDS kds = (KDS) kdsAdp.getItem(position);
                if (kds != null) {
                    intent.setClass(aty, KDSAty.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("kds", kds);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        lvPrinterPlan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                KitchenStall kitchenStall = (KitchenStall) kichenStallsAdp.getItem(position);
//                if (kitchenStall != null) {
//                    intent.setClass(aty, KitchenStallAty.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("kitchenStall", kitchenStall);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
            }
        });

    }


    /**
     * 获取门店打印机列表
     */
    private void getPrinterList() {
        try {
            PrinterDataController.cleanPrinterData();
            SystemService systemService = SystemService.getInstance();
            systemService.getPrinterList(new ResultCallback<List<Printer>>() {
                @Override
                public void onResult(List<Printer> result) {
                    PrinterDataController.printerList = result;
                    getKDSList();
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("打印机列表获取失败,", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取门店KDS列表
     */
    private void getKDSList() {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.getKDSList(new ResultCallback<List<KDS>>() {
                @Override
                public void onResult(List<KDS> result) {
                    PrinterDataController.setKdsList(result);
                    getKitchenStalls();
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("打印机列表获取失败,", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取门店档口列表
     */
    private void getKitchenStalls() {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.getKitchenStalls(new ResultCallback<List<KitchenStall>>() {
                @Override
                public void onResult(List<KitchenStall> result) {
                    PrinterDataController.setKitchenStallList(result);
                    setData();
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("打印机列表获取失败,", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    @OnClick( {R.id.iv_add_printer, R.id.img_add_kds} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_printer:
                intent.setClass(aty, PrinterInfoAty.class);
                startActivity(intent);
                break;
            case R.id.img_add_kds:
                intent.setClass(aty, KDSAty.class);
                startActivity(intent);
                break;
        }
    }
}
