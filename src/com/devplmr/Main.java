package com.devplmr;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Main
{
	static DbHandler dbHandler = null;

	enum Days
	{
		MONDAY("Понедельник"),
		TUESDAY("Вторник"),
		WEDNESDAY("Среда"),
		THURSDAY("Четверг"),
		FRIDAY("Пятница"),
		SATURDAY("Суббота");

		public final String name;

		Days(String name)
		{
			this.name = name;
		}
	}

	public static void main(String[] args)
	{
		File folder = new File("C:\\Users\\Ilya\\IdeaProjects\\mirea_parser\\files\\");
		File[] listOfFiles = folder.listFiles();
		List<String> groups;

		try
		{
			dbHandler = DbHandler.getInstance();
		}
		catch (SQLException throwables)
		{
			throwables.printStackTrace();
		}

		try
		{
			if (listOfFiles != null)
			{
				for (File file : listOfFiles)
				{
					if (file.isFile())
					{
						System.out.println("[" + file.getName() + "]");

						String filePath = file.getPath();
						groups = getGroups(filePath);

						addGroupsToDb(groups);
						addScheduleToDb(groups, filePath);
					}
				}
			}
		}
		catch (IOException | InvalidFormatException e)
		{
			e.printStackTrace();
		}
	}

	public static void printGroups(List<String> groups)
	{
		for (String group: groups)
		{
			System.out.println(group);
		}
	}

	public static List<String> getGroups(String file) throws IOException, InvalidFormatException
	{
		OPCPackage pkg = OPCPackage.open(new File(file));

		XSSFWorkbook wb = new XSSFWorkbook(pkg);
		XSSFSheet myExcelSheet = wb.getSheetAt(0);
		XSSFRow row = myExcelSheet.getRow(1);

		List<String> returningGroups = new ArrayList<>();

		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext())
		{
			Cell cell = cellIterator.next();

			if (cell.getCellType() == CellType.STRING)
			{
				String cellText = cell.getStringCellValue();
				if (cellText.contains("(КБ-"))
				{
					cellText = cellText.substring(0, Math.min(cellText.length(), 10));
					returningGroups.add(cellText);
				}
			}
		}
		wb.close();

		if (returningGroups.isEmpty())
		{
			System.out.println("Не удалось получить список групп.");
		}
		else
		{
			System.out.println("Группы успешно получены.");
			Collections.sort(returningGroups);
		}

		return returningGroups;
	}

	public static void addGroupsToDb(List<String> groups)
	{
		for (String group: groups)
		{
			dbHandler.addGroup(group);
		}

		System.out.println("Группы успешно добавлены в базу данных.");
	}

	public static void getSchedule(String file, String group) throws IOException, InvalidFormatException
	{
		OPCPackage pkg = OPCPackage.open(new File(file));

		XSSFWorkbook wb = new XSSFWorkbook(pkg);
		XSSFSheet myExcelSheet = wb.getSheetAt(0);
		XSSFRow row = myExcelSheet.getRow(1);

		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext())
		{
			Cell cell = cellIterator.next();

			if (cell.getCellType() == CellType.STRING)
			{
				String cellText = cell.getStringCellValue();
				if (cellText.startsWith(group))
				{
					int columnIndex = cell.getColumnIndex();
					int start = 3;
					int end = 75;

					int para = 1;
					String week = "Нечетная";
					Iterator<Days> daysIterator = Arrays.stream(Days.values()).iterator();
					Days day = Days.MONDAY;

					for (int i = start; i < end; i++)
					{
						XSSFRow currentRow = myExcelSheet.getRow(i);
						String subject = "-";
						String subject_type = "-";
						String teacher = "-";
						String cabinet = "-";

						try
						{
							subject = currentRow.getCell(columnIndex).toString();
							subject_type = currentRow.getCell(columnIndex + 1).toString();
							teacher = currentRow.getCell(columnIndex + 2).toString();
							cabinet = currentRow.getCell(columnIndex + 3).toString();
						}
						catch (NullPointerException e)
						{
							// PASS
						}

						int actual = i - 3;
						if (actual % 12 == 0) // Деление по дням
						{
							day = daysIterator.next();
							para = 1;
						}

						if (subject.length() == 0)
						{
							subject = "-";
						}
						if (subject_type.length() == 0)
						{
							subject_type = "-";
						}
						if (teacher.length() == 0)
						{
							teacher = "-";
						}
						if (cabinet.length() == 0)
						{
							cabinet = "-";
						}

						dbHandler.addSchedule(group, para, day.name, week, subject, subject_type, teacher, cabinet);

						if (actual % 2 != 0) // Деление на недели
						{
							para++;
							week = "Нечетная";
						}
						else
						{
							week = "Четная";
						}
					}
				}
			}
		}
		wb.close();
		System.out.println("Расписание добавлено: " + group);
	}

	public static void addScheduleToDb(List<String> groups, String file) throws IOException, InvalidFormatException
	{
		for (String group: groups)
		{
			getSchedule(file, group);
		}
	}
}
