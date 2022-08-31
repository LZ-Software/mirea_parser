BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "Week" (
	"id"	INTEGER NOT NULL UNIQUE,
	"name"	TEXT(10) NOT NULL UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Days" (
	"id"	INTEGER NOT NULL UNIQUE,
	"name"	TEXT(15) NOT NULL UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Schedule" (
	"id"	INTEGER NOT NULL UNIQUE,
	"group_id"	INTEGER NOT NULL,
	"day_id"	INTEGER NOT NULL,
	"week_id"	INTEGER NOT NULL,
	"para"	INTEGER NOT NULL,
	"subject"	TEXT(50),
	"subject_type"	TEXT(5),
	"teacher"	TEXT(25),
	"cabinet"	TEXT(15),
	FOREIGN KEY("group_id") REFERENCES "Groups"("id") ON DELETE CASCADE,
	FOREIGN KEY("day_id") REFERENCES "Days"("id") ON DELETE CASCADE,
	FOREIGN KEY("week_id") REFERENCES "Week"("id") ON DELETE CASCADE,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Groups" (
	"id"	INTEGER NOT NULL UNIQUE,
	"name"	TEXT(10) NOT NULL UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT)
);
INSERT INTO "Week" ("id","name") VALUES (1,'Нечетная'),
 (2,'Четная');
INSERT INTO "Days" ("id","name") VALUES (1,'Понедельник'),
 (2,'Вторник'),
 (3,'Среда'),
 (4,'Четверг'),
 (5,'Пятница'),
 (6,'Суббота');
COMMIT;
