package igrek.todotree.mock;

import android.content.Context;
import android.content.SharedPreferences;

import igrek.todotree.service.preferences.Preferences;
import igrek.todotree.service.preferences.PropertyDefinition;

public class MockedPreferences extends Preferences {
	
	public MockedPreferences(Context context) {
		super(context);
	}
	
	@Override
	protected SharedPreferences createSharedPreferences(Context context) {
		return null;
	}
	
	@Override
	public void saveAll() {
	}
	
	@Override
	public <T> T getValue(PropertyDefinition propertyDefinition, Class<T> clazz) {
		if (clazz.equals(Boolean.class)) {
			return (T) Boolean.FALSE;
		}
		return null;
	}
	
	@Override
	public void clear() {
	}
	
	@Override
	public boolean exists(String name) {
		return false;
	}
}
