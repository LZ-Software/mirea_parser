SELECT s.para, s.subject, s.subject_type, s.teacher, s.cabinet FROM Schedule s
JOIN Groups g ON g.id = s.group_id
JOIN Days d ON d.id = s.day_id
JOIN Week w ON w.id = s.week_id
WHERE g.name = 'БСБО-05-20' AND w.name = 'Нечетная' AND d.id = 3;

SELECT g.name, s.subject, d.name, s.para, w.name, s.subject_type, s.cabinet FROM Schedule s
JOIN Groups g ON g.id = s.group_id
JOIN Week w ON w.id = s.week_id
JOIN Days d ON d.id = s.day_id
WHERE d.name = 'Четверг' AND w.name = 'Четная' AND (s.subject LIKE '%Линейная%') AND para = 3 AND (s.cabinet LIKE '%Д%');

