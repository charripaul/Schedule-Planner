package Runners;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Models.Admin;
import Models.DataLock;
import Models.ModelControl;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage; 

public class test {
	public static void main(String args[]) {
		String s = DataLock.encrypt("");
		System.out.println(s);
		System.out.println(DataLock.decrypt(s));
	}
}
