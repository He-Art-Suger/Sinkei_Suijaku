import java.util.Calendar;

public class aaa {

	public static void main(String[] args) {
		int second = Calendar.getInstance().get(Calendar.SECOND);//秒
	    int minute = Calendar.getInstance().get(Calendar.MINUTE);//分
	    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);//時
	    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);//日
	    int month = Calendar.getInstance().get(Calendar.MONTH)+1;//月
	    int year = Calendar.getInstance().get(Calendar.YEAR);//年

	    System.out.println(year + ", " + month + ", " + day + ", " + hour + "," + minute + ", " + second );
	}

}
