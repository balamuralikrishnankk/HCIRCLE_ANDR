package Utils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;


public class Methods {
	private static ProgressDialog progressDialog;private static Dialog pgDialog;
	private static Context context;

	/***This method is used to check email id is valid or not***/
	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	/**
	 * Convert image to base64 string.
	 * 
//	 * @param Bitmap image
	 * @return String stringConvertedImage
	 */
	public static String convertImageToBase64(Bitmap img) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		byte[] byteArrayImage = baos.toByteArray();
		String temp = null;
		try {
			temp = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	/***converts dip to pixels***/
	public static float dipToPixels(Context context, float dipValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
				metrics);
	}

	/**This method is used to show short toast**/
	public static void toastShort(String msg, Context ctx) {
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}
	public static void customToastLong(String msg,Context ctx,float size)
	{
		TextView textView=new TextView(ctx);
		textView.setText(msg);
		textView.setTextSize(size);
//		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.getResources().getDimension(R.dimen.app_new));
//		textView.setTextSize(ctx.getResources().getDimension(R.dimen.text_size_small));
		textView.setTypeface(Typeface.DEFAULT_BOLD);
		Toast toast = new Toast(ctx);  
		toast.setDuration(Toast.LENGTH_LONG);  
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0); 
		toast.setView(textView);
		toast.show();
	}
	/**This method is used to show long toast**/
	public static void toastLong(String msg, Context ctx) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}
    public static void showSnackbar(String msg,Activity activity){
		SnackbarManager.show(
				Snackbar.with(activity.getApplicationContext()) // context
						.type(SnackbarType.MULTI_LINE) // Set is as a multi-line snackbar
						.text(""+msg) // text to be displayed
						.duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
						.animation(false) // don't animate it
				, activity); // where it is displayed
	}
	/**This method is used to show progress dialog**/
	public static void showProgressDialog(final Context ctx) {
		context = ctx;
		closeProgressDialog();
		pgDialog=new Dialog(ctx);
		pgDialog.setCancelable(false);

		pgDialog.show();
	}
	
	/**This method is used to show progress dialog for payments only**/
	public static void showProgressDialog(Context ctx, String message) {
		context = ctx;
		closeProgressDialog();
		progressDialog = ProgressDialog.show(context, "", "Please Wait", true);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	/**This method is used to close progress dialog**/
	public static void closeProgressDialog() {
		if(pgDialog!=null){
			pgDialog.dismiss();
		}
		if (progressDialog != null && progressDialog.isShowing() && context != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	public static BigDecimal convertTaxtoDollar(String price, String tax)
	{
		double dPrice = Double.valueOf(price);
		if(tax==null)
		{
			tax="0.0";
		}
		double dTax = Double.valueOf(tax);
		double totalTax = (dPrice) * (dTax/100) ;
		BigDecimal bdTotalTax = new BigDecimal(totalTax).setScale(2, RoundingMode.HALF_UP);
		return (bdTotalTax);
	}
	/**Perform tax calculations and calculate Total Amount**
//	 * @param String price
//	 * @param String tax
//	 * @param String quantity
	 * @return BigDecimal calculated total amount of each product including tax in BigDecimal type*/
	public static BigDecimal calculateTotalOfEachProduct(String price, String tax, String quantity){

		double dPrice = Double.valueOf(price);
		double dTax = Double.valueOf(tax);
		double dQuantity = Double.valueOf(quantity);

		double totalTax = (dPrice) * (dTax/100) ;

		double totalPrice = (((dPrice) + (totalTax)) * dQuantity);

		BigDecimal bdTotalPrice = new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP);

		return (bdTotalPrice);
	}
	
	/**Perform tax calculations and calculate Total Amount of each product 
	 * including tax only and excluding quantity**
//	 * @param String price
//	 * @param String tax
	 * @return calculated total amount of each product including tax and excluding quantity*/
	public static BigDecimal calculateTotalOfEachProduct(String price, String tax){

		double dPrice = Double.valueOf(price);
		if(tax==null){
			tax="0.0";
		}
		double dTax = Double.valueOf(tax);

		double totalTax = (dPrice) * (dTax/100) ;

		double totalPrice = (((dPrice) + (totalTax)));

		BigDecimal bdTotalPrice = new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP);

		return (bdTotalPrice);
	}
}
