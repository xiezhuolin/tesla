package cn.acewill.pos.next.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.model.event.PrinterContentEvent;
import cn.acewill.pos.next.model.printer.ModleStyle;
import cn.acewill.pos.next.model.printer.PrintContent;
import cn.acewill.pos.next.model.printer.PrintInfo;
import cn.acewill.pos.next.model.printer.PrinterStyle;
import cn.acewill.pos.next.ui.adapter.DragListAdapter;
import cn.acewill.pos.next.ui.adapter.PrinterModeAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.DragListView;
import cn.acewill.pos.next.widget.SegmentedRadioGroup;

public class SettingPrinterAty extends BaseActivity implements OnCheckedChangeListener{
	private GridView gridItemContent;
	private DragListView dragListView;

	private String [] moduleNameArr = {"标题","内容","电话","总计"};
	private ArrayList<String> moduleNameList = new ArrayList<>();

	private List<PrinterStyle> printerList = new ArrayList<PrinterStyle>();
	private PrinterModeAdp printerModeAdp;

	private String storeName = "我是店";
	private String tableInfo = "我是桌台信息";
	private String totalSum = "总计 ：    222";
	private String phoneNumber = "13111111111";
	private int [] modeStyle = {ModleStyle.MODE_STORE_NAME,ModleStyle.MODE_TABLE_INFO,ModleStyle.MODE_TOTAL_SUM,ModleStyle.MODE_PHONE_NUMBER};
	private ArrayList<Integer> moduldStyleList = new ArrayList<>();

	private ArrayList<PrintContent> printContent = new ArrayList<PrintContent>();
	private ArrayList<PrintContent> storeInfo = new ArrayList<PrintContent>();
	private DragListAdapter adapter;
	private PrintContent printContentSelect = null;
	private int selectPosition = -1;

	private SegmentedRadioGroup segment_show_location;
	private SegmentedRadioGroup segment_text_size;
	private SegmentedRadioGroup segment_text_style;
	private RadioButton button_left;
	private RadioButton button_center;
	private RadioButton button_right;
	private RadioButton button_one;
	private RadioButton button_two;
	private RadioButton button_there;
	private RadioButton button_nomer;
	private RadioButton button_bold;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentXml(R.layout.aty_setting_printer);
		myApplication.addPage(SettingPrinterAty.this);
		setTitle(ToolsUtils.returnXMLStr("print_settings"));
		EventBus.getDefault().register(this);

		gridItemContent = (GridView) findViewById(R.id.grid_item_content);
		segment_show_location = (SegmentedRadioGroup) findViewById(R.id.segment_show_location);
		segment_text_size = (SegmentedRadioGroup) findViewById(R.id.segment_text_size);
		segment_text_style = (SegmentedRadioGroup) findViewById(R.id.segment_text_style);
		button_left = (RadioButton) findViewById(R.id.button_left);
		button_center = (RadioButton) findViewById(R.id.button_center);
		button_right = (RadioButton) findViewById(R.id.button_right);
		button_one = (RadioButton) findViewById(R.id.button_one);
		button_two = (RadioButton) findViewById(R.id.button_two);
		button_there = (RadioButton) findViewById(R.id.button_there);
		button_nomer = (RadioButton) findViewById(R.id.button_nomer);
		button_bold = (RadioButton) findViewById(R.id.button_bold);

		segment_show_location.setOnCheckedChangeListener(this);
		segment_text_size.setOnCheckedChangeListener(this);
		segment_text_style.setOnCheckedChangeListener(this);

		for (int i = 0; i < moduleNameArr.length; i++) {
			moduleNameList.add(moduleNameArr[i]);
		}

		for (int i = 0; i < modeStyle.length; i++) {
			moduldStyleList.add(modeStyle[i]);
		}

		printerList = addPrinterInfo(moduleNameList,moduldStyleList);
		printerModeAdp = new PrinterModeAdp(this);
		printerModeAdp.setData(printerList);
		gridItemContent.setAdapter(printerModeAdp);


		gridItemContent.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				if(printerList != null && printerList.size() >0)
				{
					PrinterStyle content = printerList.get(position);
					if(content != null)
					{
						if(content.enableStatus == PrinterStyle.EnableStatus.UNSELECTED)
						{
							content.enableStatus =  PrinterStyle.EnableStatus.SELECTED;
						}
						else
						{
							content.enableStatus =  PrinterStyle.EnableStatus.UNSELECTED;
						}
					}
//					storeInfo =  addStoreInfo(printerList);
//					adapter = new DragListAdapter(SettingPrinterAty.this, storeInfo);
//					dragListView.setAdapter(adapter);

					printerModeAdp.notifyDataSetChanged();
				}
			}
		});

//		dragListView = (DragListView)findViewById(R.id.other_drag_list);
//		storeInfo = addStoreInfo(printerList);
//		adapter = new DragListAdapter(this, storeInfo);
//		dragListView.setAdapter(adapter);

		dragListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {
				// TODO Auto-generated method stub
				printContentSelect = (PrintContent)adapter.getItem(position);
				selectPosition = position;
				adapter.setSelectPosition(selectPosition);


				PrinterStyle printerStyleList = printContentSelect.printerStyle;
				if(printerStyleList != null)
				{
					//设置对齐方式
					if(printerStyleList.showLocation == PrinterStyle.ShowLocation.Left)
					{
						button_left.setChecked(true);
					}
					else if(printerStyleList.showLocation == PrinterStyle.ShowLocation.Center)
					{
						button_center.setChecked(true);
					}
					else if(printerStyleList.showLocation == PrinterStyle.ShowLocation.Right)
					{
						button_right.setChecked(true);
					}
					if(printerStyleList.textSize == PrinterStyle.TextSize.NormalSize)
					{
						button_one.setChecked(true);
					}
					else if(printerStyleList.textSize == PrinterStyle.TextSize.TwoXSize)
					{
						button_two.setChecked(true);
					}
					else if(printerStyleList.textSize == PrinterStyle.TextSize.ThereXSize)
					{
						button_there.setChecked(true);
					}
					//设置字体样式
					if(printerStyleList.textStyle == PrinterStyle.TextStyle.Normal)
					{
						button_nomer.setChecked(true);
					}
					else if(printerStyleList.textStyle == PrinterStyle.TextStyle.Bold)
					{
						button_bold.setChecked(true);
					}
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private List<PrinterStyle> addPrinterInfo(ArrayList<String> moduleNameList,ArrayList<Integer> moduleStyleList)
	{
		List<PrinterStyle> printerList = new ArrayList<>();
		for (int i = 0; i < moduleNameList.size(); i++) {
			PrinterStyle content = new PrinterStyle();
			content.moduleName = moduleNameList.get(i);
			content.modeStyle = moduleStyleList.get(i);
			content.enableStatus = PrinterStyle.EnableStatus.SELECTED;
			content.showLocation = PrinterStyle.ShowLocation.Center;
			content.textStyle = PrinterStyle.TextStyle.Normal;
			content.textSize = PrinterStyle.TextSize.NormalSize;
			printerList.add(content);
		}
		return printerList.size() > 0 ? printerList:null;
	}


	private ArrayList<PrintContent> addStoreInfo(List<PrinterStyle> printerList)
	{
		ArrayList<PrintContent> printContent = new ArrayList<PrintContent>();
		for (int i = 0; i < printerList.size(); i++) {
			PrinterStyle printItem = printerList.get(i);
			if(printItem != null)
			{
				if(printItem.enableStatus == PrinterStyle.EnableStatus.SELECTED)
				{
					PrintContent content = new PrintContent();
					content.modeStyle = printItem.modeStyle;
					PrintInfo info = new PrintInfo();
					if(content.modeStyle == ModleStyle.MODE_STORE_NAME)
					{
						info.modeStr = storeName;
					}
					else if(content.modeStyle == ModleStyle.MODE_TABLE_INFO)
					{
						info.modeStr = tableInfo;
					}
					else if(content.modeStyle == ModleStyle.MODE_TOTAL_SUM)
					{
						info.modeStr = totalSum;
					}
					else if(content.modeStyle == ModleStyle.MODE_PHONE_NUMBER)
					{
						info.modeStr = phoneNumber;
					}
					else
					{
						info.modeStr = printItem.moduleName;
					}
					content.printerStyle = printItem;
					content.modeInfo.add(info);
					printContent.add(content);
				}
			}
		}
		return printContent;
	}

	@Subscribe
	private void AddPrinterContent(PrinterContentEvent event) {
		switch (event.getAction()) {
			case Constant.PrinterContentStyle.ADD_PRINTER_CONTENT:
				moduleNameList.add(event.getContent());
				moduldStyleList.add(ModleStyle.MODE_MYSELF_ADD);
				printerList = addPrinterInfo(moduleNameList,moduldStyleList);
				printerModeAdp.setData(printerList);
				gridItemContent.setAdapter(printerModeAdp);

//				storeInfo =  addStoreInfo(printerList);
//				adapter = new DragListAdapter(SettingPrinterAty.this, storeInfo);
//				dragListView.setAdapter(adapter);

				adapter.notifyDataSetChanged();
				printerModeAdp.notifyDataSetChanged();
				break;
		}
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		//对齐方式radio监听
		if (group == segment_show_location) {
			if(selectPosition == -1)
			{
				System.out.println("请先选择一项");
				return;
			}
			else
			{
				PrinterStyle contentStyle = ((PrintContent)adapter.getItem(selectPosition)).printerStyle;
				if(contentStyle != null)
				{
					if(checkedId == R.id.button_left)
					{
						contentStyle.showLocation = PrinterStyle.ShowLocation.Left;
					}
					else if(checkedId == R.id.button_center)
					{
						contentStyle.showLocation = PrinterStyle.ShowLocation.Center;
					}
					else if(checkedId == R.id.button_right)
					{
						contentStyle.showLocation = PrinterStyle.ShowLocation.Right;
					}
					adapter.notifyDataSetChanged();
				}
			}
		}
		//字体大小radio监听
		if (group == segment_text_size) {
			if(selectPosition == -1)
			{
				System.out.println("请先选择一项");
				return;
			}
			else
			{
				PrinterStyle contentStyle = ((PrintContent)adapter.getItem(selectPosition)).printerStyle;
				if(contentStyle != null)
				{
					if(checkedId == R.id.button_one)
					{
						contentStyle.textSize = PrinterStyle.TextSize.NormalSize;
					}
					else if(checkedId == R.id.button_two)
					{
						contentStyle.textSize = PrinterStyle.TextSize.TwoXSize;
					}
					else if(checkedId == R.id.button_there)
					{
						contentStyle.textSize = PrinterStyle.TextSize.ThereXSize;
					}
					adapter.notifyDataSetChanged();
				}
			}
		}
		//字体样式radio监听
		if (group == segment_text_style) {
			if(selectPosition == -1)
			{
				System.out.println("请先选择一项");
				return;
			}
			else
			{
				PrinterStyle contentStyle = ((PrintContent)adapter.getItem(selectPosition)).printerStyle;
				if(contentStyle != null)
				{
					if(checkedId == R.id.button_nomer)
					{
						contentStyle.textStyle = PrinterStyle.TextStyle.Normal;
					}
					else if(checkedId == R.id.button_bold)
					{
						contentStyle.textStyle = PrinterStyle.TextStyle.Bold;
					}
					adapter.notifyDataSetChanged();
				}
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
