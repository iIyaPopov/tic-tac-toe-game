package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TestRunner {
	
    public static void main(String[] args) {
        JUnitCore runner = new JUnitCore();
		
        Result result = runner.run(ServerTest.class);
		System.out.println("ServerTest");
        System.out.println("Run: " + result.getRunCount());
        System.out.println("Failed: " + result.getFailureCount());
        System.out.println("Ignored: " + result.getIgnoreCount());
        System.out.println("Success: " + result.wasSuccessful());
		
		result = runner.run(UserListTest.class);
		System.out.println();
		System.out.println("UserListTest");
        System.out.println("Run: " + result.getRunCount());
        System.out.println("Failed: " + result.getFailureCount());
        System.out.println("Ignored: " + result.getIgnoreCount());
        System.out.println("Success: " + result.wasSuccessful());
        
        result = runner.run(GameModelTest.class);
		System.out.println();
		System.out.println("GameModelTest");
        System.out.println("Run: " + result.getRunCount());
        System.out.println("Failed: " + result.getFailureCount());
        System.out.println("Ignored: " + result.getIgnoreCount());
        System.out.println("Success: " + result.wasSuccessful());
    }
}
