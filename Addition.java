package com.modernization.arithmetic;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Addition Calculator Application
 * 
 * Purpose: Demonstrates basic arithmetic operations with input/output handling
 * Converted from COBOL program ADDITION.cob
 * 
 * @author Modernization Team
 * @version 1.0
 * @since 2026-05-28
 */
public class Addition {
    
    private static final Logger LOGGER = Logger.getLogger(Addition.class.getName());
    private static final int MAX_DIGITS = 4;
    private static final int MAX_VALUE = 9999;
    private static final int MIN_VALUE = 0;
    
    private final Scanner scanner;
    private final ArithmeticProcessor processor;
    
    /**
     * Constructor initializing the Addition calculator
     */
    public Addition() {
        this.scanner = new Scanner(System.in);
        this.processor = new ArithmeticProcessor();
    }
    
    /**
     * Main entry point of the application
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Addition app = new Addition();
        try {
            app.execute();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Application error occurred", e);
            System.err.println("An error occurred: " + e.getMessage());
            System.exit(1);
        } finally {
            app.cleanup();
        }
    }
    
    /**
     * Main execution logic - corresponds to COBOL PROCEDURE DIVISION
     */
    public void execute() {
        LOGGER.info("Starting Addition Calculator");
        
        // Accept first number (corresponds to COBOL NUM1)
        int firstNumber = acceptNumber("ENTER FIRST NUMBER (4 DIGITS): ");
        
        // Accept second number (corresponds to COBOL NUM2)
        int secondNumber = acceptNumber("ENTER SECOND NUMBER (4 DIGITS): ");
        
        // Perform addition (corresponds to COBOL ADD NUM1 TO NUM2 GIVING RESULT)
        int result = processor.add(firstNumber, secondNumber);
        
        // Display result (corresponds to COBOL DISPLAY)
        displayResult(result);
        
        LOGGER.info("Addition Calculator completed successfully");
    }
    
    /**
     * Accepts a number from user input with validation
     * Corresponds to COBOL ACCEPT statement with PIC 9(4) validation
     * 
     * @param prompt the prompt message to display
     * @return validated integer input
     */
    private int acceptNumber(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int number = scanner.nextInt();
                
                // Validate input range (COBOL PIC 9(4) constraint)
                if (number < MIN_VALUE || number > MAX_VALUE) {
                    System.out.println("Error: Number must be between " + 
                                     MIN_VALUE + " and " + MAX_VALUE);
                    continue;
                }
                
                return number;
                
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a numeric value.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    /**
     * Displays the calculation result
     * Corresponds to COBOL DISPLAY with PIC Z(4)9 formatting
     * 
     * @param result the calculation result to display
     */
    private void displayResult(int result) {
        // Format with leading spaces suppression (COBOL PIC Z(4)9 behavior)
        String formattedResult = String.format("%5d", result).replaceFirst("^ +", "");
        System.out.println("THE SUM IS: " + formattedResult);
    }
    
    /**
     * Cleanup resources
     * Corresponds to COBOL STOP RUN
     */
    private void cleanup() {
        if (scanner != null) {
            scanner.close();
        }
        LOGGER.info("Resources cleaned up");
    }
}

/**
 * Arithmetic Processor - Encapsulates arithmetic operations
 * Demonstrates OOP principle of Single Responsibility
 */
class ArithmeticProcessor {
    
    private static final Logger LOGGER = Logger.getLogger(ArithmeticProcessor.class.getName());
    private static final int MAX_RESULT = 99999; // COBOL PIC 9(5)
    
    /**
     * Adds two numbers with overflow checking
     * Corresponds to COBOL ADD statement with GIVING clause
     * 
     * @param num1 first operand
     * @param num2 second operand
     * @return sum of the two numbers
     * @throws ArithmeticException if result exceeds maximum value
     */
    public int add(int num1, int num2) {
        LOGGER.fine(() -> String.format("Adding %d + %d", num1, num2));
        
        long result = (long) num1 + (long) num2;
        
        // Check for overflow (COBOL PIC 9(5) constraint)
        if (result > MAX_RESULT) {
            String errorMsg = String.format(
                "Arithmetic overflow: %d + %d = %d exceeds maximum value %d",
                num1, num2, result, MAX_RESULT
            );
            LOGGER.warning(errorMsg);
            throw new ArithmeticException(errorMsg);
        }
        
        return (int) result;
    }
    
    /**
     * Gets the maximum allowed result value
     * 
     * @return maximum result value
     */
    public int getMaxResult() {
        return MAX_RESULT;
    }
}


