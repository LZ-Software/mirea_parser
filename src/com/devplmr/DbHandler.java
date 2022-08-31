package com.devplmr;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;

public class DbHandler
{
	// Константа, в которой хранится адрес подключения
	private static final String CON_STR = "jdbc:sqlite:C:/Users/Ilya/IdeaProjects/mirea_parser/db/mirea.db";

	// Используем шаблон одиночка, чтобы не плодить множество
	// экземпляров класса DbHandler
	private static DbHandler instance = null;

	public static synchronized DbHandler getInstance() throws SQLException
	{
		if (instance == null)
			instance = new DbHandler();
		return instance;
	}

	// Объект, в котором будет храниться соединение с БД
	private Connection connection;

	private DbHandler() throws SQLException
	{
		// Регистрируем драйвер, с которым будем работать
		// в нашем случае Sqlite
		DriverManager.registerDriver(new JDBC());
		// Выполняем подключение к базе данных
		this.connection = DriverManager.getConnection(CON_STR);
	}

	// Добавление в БД
	public void addGroup(String group)
	{
		// Создадим подготовленное выражение, чтобы избежать SQL-инъекций
		try (PreparedStatement statement = this.connection.prepareStatement(
				"INSERT INTO Groups(`name`) VALUES(?)"))
		{
			statement.setString(1, group);
			// Выполняем запрос
			statement.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void addSchedule(String group, int para, String day, String week, String subject, String subject_type, String teacher, String cabinet)
	{
		try (PreparedStatement statement = this.connection.prepareStatement(
				"INSERT INTO Schedule(`group_id`, `para`, `day_id`, `week_id`, `subject`, `subject_type`, `teacher`, `cabinet`) " +
						"VALUES ((SELECT id FROM Groups WHERE name = ?), ?, (SELECT id FROM Days WHERE name = ?), " +
						"(SELECT id FROM Week WHERE name = ?), ?, ?, ?, ?)"))
		{
			statement.setString(1, group);
			statement.setInt(2, para);
			statement.setString(3, day);
			statement.setString(4, week);
			statement.setString(5, subject);
			statement.setString(6, subject_type);
			statement.setString(7, teacher);
			statement.setString(8, cabinet);
			// Выполняем запрос
			statement.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
