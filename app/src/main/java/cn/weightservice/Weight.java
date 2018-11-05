package cn.weightservice;

import android.os.Parcel;
import android.os.Parcelable;



public class Weight  implements Parcelable{

	private int tareWeight; //Ƥ��
	private int netWeight;  //����
	private int numberTareWeight; //����Ƥ��
	private int zeroOKMark;  //��������Ƿ�����
	private int signMark;   //����   ture Ϊ��  false ��
	private int zeroMark ;  //��λ��־
	private int steadyMark; //�ȶ���־
	private int tareMark;   //ȥƤ״̬
	private int overLoadMark;  //����״̬
    private int openZeroHighMark;  //�������߱�־
 	private int openZeroLowMark;  //�������ͱ�־
 	private int pointnumber;  //С�����λ��
 	private int  maxRange;   //������̷�Χ
    private int  minDivision; //��С�ֶ�ֵ
    private long currentTM; //��ǰ��ʱ�䰴�������; ���һ�ζ�����ʱ��ʱ��ֵ
	
 	

	public  Weight(int tareWeight,int netWeight,int numberTareWeight,int zeroOKMark,int signMark,int zeroMark,int steadyMark,int tareMark 
 			   ,int overLoadMark ,int openZeroHighMark ,int openZeroLowMark,int pointnumber,int maxRange,int minDivision,long currentTM) {
		this.tareWeight=tareWeight;
		this.netWeight=netWeight;
		this.numberTareWeight=numberTareWeight;
		this.zeroOKMark=zeroOKMark;
		this.signMark=signMark;
		this.zeroMark=zeroMark;
		this.steadyMark=steadyMark;
		this.tareMark=tareMark;
		this.overLoadMark=overLoadMark;
		this.openZeroHighMark=openZeroHighMark;
		this.openZeroLowMark=openZeroLowMark;
		this.pointnumber=pointnumber;
		this.maxRange=maxRange;
		this.minDivision=minDivision;
		this.currentTM=currentTM;
	}
 	
	public int getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}

	public int getMinDivision() {
		return minDivision;
	}

	public void setMinDivision(int minDivision) {
		this.minDivision = minDivision;
	}

	public Weight(){
 		
 	}
	
	public int getPointnumber() {
		return pointnumber;
	}
	public void setPointnumber(int pointnumber) {
		this.pointnumber = pointnumber;
	}
	public int getTareWeight() {
		return tareWeight;
	}
	public void setTareWeight(int tareWeight) {
		this.tareWeight = tareWeight;
	}
	public int getNetWeight() {
		return netWeight;
	}
	public void setNetWeight(int netWeight) {
		this.netWeight = netWeight;
	}

	public int getNumberTareWeight() {
		return numberTareWeight;
	}
	public void setNumberTareWeight(int numberTareWeight) {
		this.numberTareWeight = numberTareWeight;
	}
	public int getZeroOKMark() {
		return zeroOKMark;
	}
	public void setZeroOKMark(int zeroOKMark) {
		this.zeroOKMark = zeroOKMark;
	}

	public int getSignMark() {
		return signMark;
	}
	public void setSignMark(int signMark) {
		this.signMark = signMark;
	}
	public int getZeroMark() {
		return zeroMark;
	}
	public void setZeroMark(int zeroMark) {
		this.zeroMark = zeroMark;
	}
	public int getSteadyMark() {
		return steadyMark;
	}
	public void setSteadyMark(int steadyMark) {
		this.steadyMark = steadyMark;
	}
	public int getTareMark() {
		return tareMark;
	}
	public void setTareMark(int tareMark) {
		this.tareMark = tareMark;
	}
	public int getOverLoadMark() {
		return overLoadMark;
	}
	public void setOverLoadMark(int overLoadMark) {
		this.overLoadMark = overLoadMark;
	}

	public int getOpenZeroHighMark() {
		return openZeroHighMark;
	}
	public void setOpenZeroHighMark(int openZeroHighMark) {
		this.openZeroHighMark = openZeroHighMark;
	}
	public int getOpenZeroLowMark() {
		return openZeroLowMark;
	}
	public void setOpenZeroLowMark(int openZeroLowMark) {
		this.openZeroLowMark = openZeroLowMark;
	}
	public long getCurrentTM() {
		return currentTM;
	}

	public void setCurrentTM(long currentTM) {
		this.currentTM = currentTM;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

		dest.writeInt(tareWeight);//Ƥ��
		dest.writeInt(netWeight);//����
		dest.writeInt(numberTareWeight);//����Ƥ��
		dest.writeInt(zeroOKMark);//��������Ƿ�����
		dest.writeInt(signMark);//����   ture Ϊ��  false ��
		dest.writeInt(zeroMark);//��λ��־
		dest.writeInt(steadyMark);//�ȶ���־
		dest.writeInt(tareMark); //ȥƤ״̬
		dest.writeInt(overLoadMark);//����״̬
		dest.writeInt(openZeroHighMark);//�������߱�־
		dest.writeInt(openZeroLowMark);//�������ͱ�־
		dest.writeInt(pointnumber);//С�����λ��
		dest.writeInt(maxRange);//������̷�Χ
		dest.writeInt(minDivision);//��С�ֶ�ֵ
		dest.writeLong(currentTM);//����ȡ������ʱ��

	}
	
	// ���һ����̬��Ա,��ΪCREATOR,�ö���ʵ����Parcelable.Creator�ӿ�
		public static final Creator<Weight> CREATOR
			= new Creator<Weight>() //��
		{
			@Override
			public Weight createFromParcel(Parcel source)
			{
				// ��Parcel�ж�ȡ���ݣ�����Weight����
				return new Weight(source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readInt(),
						source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readInt(),source.readInt(),
						source.readInt(),source.readInt(),source.readLong()		);
			}

			@Override
			public Weight[] newArray(int size)
			{
				return new Weight[size];
			}
		};
}
