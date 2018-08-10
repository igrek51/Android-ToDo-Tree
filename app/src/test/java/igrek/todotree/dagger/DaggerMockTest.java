package igrek.todotree.dagger;


import org.junit.Before;
import org.junit.Test;

import igrek.todotree.activity.MainActivity;
import igrek.todotree.dagger.test.BaseDaggerTest;

import static org.mockito.Mockito.mock;

public class DaggerMockTest extends BaseDaggerTest {
	
	@Before
	public void setUp() {
		MainActivity activity = mock(MainActivity.class);
		// Dagger init test
		DaggerIOC.initTest(null, activity);
		DaggerIOC.getTestComponent().inject(this);
	}
	
	@Test
	public void testMocks() {
		
		infoService.showInfo("DUPA");
		
	}
	
}
