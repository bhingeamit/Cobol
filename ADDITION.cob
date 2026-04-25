      *****************************************************************
      * PROGRAM-ID: ADDITION                                          *
      * PURPOSE:    DEMONSTRATE BASIC ARITHMETIC AND INPUT/OUTPUT     *
      *****************************************************************
       IDENTIFICATION DIVISION.
       PROGRAM-ID. ADDITION.

       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.

       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 NUM1         PIC 9(4)  VALUE ZEROS.
       01 NUM2         PIC 9(4)  VALUE ZEROS.
       01 RESULT       PIC 9(5)  VALUE ZEROS.
       01 DISPLAY-RES  PIC Z(4)9.

       PROCEDURE DIVISION.
       0100-START-PARA.
           DISPLAY "ENTER FIRST NUMBER (4 DIGITS): ".
           ACCEPT NUM1.

           DISPLAY "ENTER SECOND NUMBER (4 DIGITS): ".
           ACCEPT NUM2.

           ADD NUM1 TO NUM2 GIVING RESULT.

           MOVE RESULT TO DISPLAY-RES.
           DISPLAY "THE SUM IS: " DISPLAY-RES.

           STOP RUN.
