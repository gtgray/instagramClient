package tk.atna.instagramclient;

import android.os.Bundle;
import android.os.Parcelable;

public abstract class Bundlable {
	
	/**
	 * Returns object of type clazz from Bundle, as a key using name of Class clazz,
	 * or null if no source or there is no object associated with the key 
	 * or found object doesn't implement Parcelable 
	 * 
	 * @param source bundle object to get data from
	 * @param clazz type of object to get, also uses as a mapping key
	 * @return object of type clazz or null
	 */
	public static <T> T fromBundle(Bundle source, Class<T> clazz) {
		try {
			T target = source.getParcelable(clazz.toString());
			return target;
			
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns wrapped value object of type clazz into Bundle, name of Class clazz uses as a key,
	 * or null if value object doesn't implement Parcelable or null
	 * 
	 * @param value object of type clazz
	 * @param clazz type of value, also uses as a mapping key
	 * @return bundle object with value wrapped into it or null  
	 */
	public static <T> Bundle toBundle(T value, Class<T> clazz) {
		try {
			Bundle data = new Bundle();
			data.putParcelable(clazz.toString(), (Parcelable) value);
			return data;
			
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
