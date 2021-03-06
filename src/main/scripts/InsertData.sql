INSERT INTO criteria (`id`, `deleted`, `description`, `name`, `publish_date`, `reusable`) VALUES ('1001', b'0', 'This is a test', 'Programming Paradigms', '2020-05-20', b'1');
INSERT INTO criteria (`id`, `deleted`, `description`, `name`, `publish_date`, `reusable`) VALUES ('1002', b'0', 'see how well a student know about functions and methods', 'Functions and Methods', '2020-07-23', b'1');
INSERT INTO criteria (`id`, `deleted`, `description`, `name`, `publish_date`, `reusable`) VALUES ('1003', b'0', 'if the student can ..', 'Knowledge', '2020-02-23', b'1');

INSERT INTO tags (`id`, `count`, `value`) VALUES ('2001', '10', 'CS2011_OUTCOME');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2002', '15', 'CS2012_OUTCOME');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2003', '13', 'CS2013_OUTCOME');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2004', '1', 'MATH');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2005', '20', 'CS3220_OUTCOME');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2006', '3', 'PHYSICS');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2007', '1', 'CHEMISTRY');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2008', '18', 'CS4540_OUTCOME');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2009', '10', 'CS3337_OUTCOME');
INSERT INTO tags (`id`, `count`, `value`) VALUES ('2010', '13', 'CS5220_OUTCOME');

INSERT INTO users (`id`, `cin`, `first_name`, `last_name`, `password`, `username`) VALUES ('5000', '1234', 'Jane', 'Doe', 'aaaa', 'janedoe');
INSERT INTO users (`id`, `cin`, `first_name`, `last_name`, `password`, `username`) VALUES ('5001', '1235', 'Tracy', 'Lan', 'aaaa', 'tracyl');
INSERT INTO users (`id`, `cin`, `first_name`, `last_name`, `password`, `username`) VALUES ('5002', '1236', 'John', 'Doe', 'aaaa', 'johndoe');
INSERT INTO users (`id`, `cin`, `first_name`, `last_name`, `password`, `username`) VALUES ('5003', '1237', 'Josh', 'Chris', 'aaaa', 'joshc');
INSERT INTO users (`id`, `cin`, `first_name`, `last_name`, `password`, `username`) VALUES ('5004', '1238', 'Tom', 'Sawyer', 'aaaa', 'toms');

INSERT INTO rubrics (`id`, `name`, `description`, `publish_date`, `public`, `deleted`, `obsolete`, `creator_id`) VALUES ('3001', 'Rubric 1', 'this is my first rubric', '2020-12-24', b'1', b'0', b'0', 5000);
INSERT INTO rubrics (`id`, `name`, `description`, `publish_date`, `public`, `deleted`, `obsolete`, `creator_id`) VALUES ('3002', 'Rubric 2', 'fun', '2020-04-24', b'1', b'0', b'0', 5000);

INSERT INTO associations (`id`, `endpoint`, `name`, `type`) VALUES ('6001', '/api/v1/courses/cs-3220/assignments/1', 'HW1 in CS3220', 'Assignment');
INSERT INTO associations (`id`, `endpoint`, `name`, `type`) VALUES ('6002', '/api/v1/courses/cs-3220/assignments/2', 'HW2 in CS3220', 'Assignment');
INSERT INTO associations (`id`, `endpoint`, `name`, `type`) VALUES ('6003', '/api/v1/courses/cs-2011/assignments/1', 'HW1 in CS2011', 'Assignment');
INSERT INTO associations (`id`, `endpoint`, `name`, `type`) VALUES ('6004', '/api/v1/courses/cs-4961/assignments/1', 'Essay', 'Assignment');
INSERT INTO associations (`id`, `endpoint`, `name`, `type`) VALUES ('6005', '/api/v1/courses/cs-4961/assignments/2', 'Teamwork', 'Assignment');


INSERT INTO artifacts (`id`, `endpoint`, `name`, `type`) VALUES ('7001', '/api/v1/courses/cs-2011/assignments/6003/submissions/5001', 'Tracy\'s submission for CS2011 HW1', 'Submission');
INSERT INTO artifacts (`id`, `endpoint`, `name`, `type`) VALUES ('7002', '/api/v1/courses/cs-2011/assignments/6003/submissions/5000', 'Jane\'s submission for CS2011 HW1', 'Submission');
INSERT INTO artifacts (`id`, `endpoint`, `name`, `type`) VALUES ('7003', '/api/v1/courses/cs-3220/assignments/6001/submissions/5000', 'Jane\'s submission for CS3220 HW1', 'Submission');
INSERT INTO artifacts (`id`, `endpoint`, `name`, `type`) VALUES ('7004', '/api/v1/courses/cs-4961/assignments/6004/submissions/5002', 'Jane\'s submission for CS2011 HW1', 'Submission');
INSERT INTO artifacts (`id`, `endpoint`, `name`, `type`) VALUES ('7005', '/api/v1/courses/cs-4961/assignments/6005/submissions/5002', 'Jane\'s submission for CS3220 HW1', 'Submission');
