-- ----------------------------------------
-- 인사관리 시스템 테이블 생성 (MySQL 8 / Flyway: V1__hr_init.sql)
-- 명명 규칙은 수업 코드 기준 (테이블명_id, fk_자식_부모, uq_테이블_컬럼)
-- PK는 Oracle 시퀀스 대신 auto_increment
-- ----------------------------------------

-- ----------------------------------------
-- 역할(Role) : 권한(Permission)의 묶음. 사원이 아니라 계정(account)에 부여한다.
-- ----------------------------------------
create table hr_role(
	hr_role_id int primary key auto_increment    -- 역할 ID
	, role_code varchar(30) not null             -- 역할 코드 (ROLE_ 접두어)
	, role_name varchar(50) not null             -- 역할명
	, is_active tinyint(1) default 1             -- 활성 여부 (1/0)
	, constraint uq_hr_role_role_code unique(role_code)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 권한(Permission) : 실제 기능 권한. Spring Security의 GrantedAuthority로 사용한다.
-- ----------------------------------------
create table permission(
	permission_id int primary key auto_increment    -- 권한 ID
	, permission_code varchar(40) not null          -- 권한 코드 (PERMISSION_ 접두어)
	, permission_name varchar(60) not null          -- 권한명
	, permission_group varchar(20) not null         -- 권한 분류 (SECURITY/HR/ATTENDANCE/LEAVE/AUTH)
	, constraint uq_permission_permission_code unique(permission_code)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 역할별 권한 : Role과 Permission의 N:M 매핑. 체크박스 화면에서 저장된다.
-- ----------------------------------------
create table hr_role_permission(
	hr_role_permission_id int primary key auto_increment    -- 역할별 권한 ID
	, hr_role_id int not null                               -- 역할 ID
	, permission_id int not null                            -- 권한 ID
	, constraint fk_hr_role_permission_hr_role foreign key(hr_role_id) references hr_role(hr_role_id)
	, constraint fk_hr_role_permission_permission foreign key(permission_id) references permission(permission_id)
	, constraint uq_hr_role_permission unique(hr_role_id, permission_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 부서(조직) : 경영진 > 본부 > 팀 계층 조직. parent_id로 상위 조직, manager_id로 조직장을 가리킨다.
-- ----------------------------------------
create table department(
	department_id int primary key auto_increment    -- 부서 ID
	, department_code varchar(20) not null          -- 부서 코드
	, department_name varchar(60) not null          -- 부서명
	, department_level tinyint(1) not null          -- 조직 단계 (1 경영진 / 2 본부 / 3 팀)
	, parent_id int                                 -- 상위 조직 ID (최상위는 null)
	, manager_id int                                -- 조직장 사원 ID
	, is_active tinyint(1) default 1                -- 활성 여부 (1/0)
	, constraint fk_department_parent foreign key(parent_id) references department(department_id)
	, constraint uq_department_department_code unique(department_code)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 직급 : 사원 ~ 대표이사 직급과 서열. 보안 Role과는 별개 개념이다.
-- ----------------------------------------
create table job(
	job_id int primary key auto_increment    -- 직급 ID
	, job_code varchar(30) not null          -- 직급 코드
	, job_name varchar(30) not null          -- 직급명
	, job_level int not null                 -- 직급 서열 (클수록 높음, 정렬·조직장 선정에 사용)
	, is_active tinyint(1) default 1         -- 활성 여부 (1/0)
	, constraint uq_job_job_code unique(job_code)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 직책 : 대표이사·CFO·CTO·본부장·팀장 등 직책. 직책이 없으면 사원의 hr_position_id는 null.
-- ----------------------------------------
create table hr_position(
	hr_position_id int primary key auto_increment    -- 직책 ID
	, position_code varchar(50) not null             -- 직책 코드
	, position_name varchar(100) not null            -- 직책명
	, is_active tinyint(1) default 1                 -- 활성 여부 (1/0)
	, constraint uq_hr_position_position_code unique(position_code)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 사원 : 사원 자체의 인사정보. 로그인 ID/비밀번호/Role은 두지 않는다.
-- ----------------------------------------
create table emp(
	emp_id int primary key auto_increment           -- 사원 ID
	, emp_no varchar(20) not null                   -- 사번 (EMP + 입사연도 + 순번)
	, ename varchar(30) not null                    -- 사원명
	, email varchar(100)                            -- 이메일
	, phone varchar(13)                             -- 연락처
	, hiredate date not null                        -- 입사일
	, resign_date date                              -- 퇴직일
	, department_id int not null                    -- 소속 부서
	, job_id int not null                           -- 직급
	, hr_position_id int                            -- 직책 (없으면 null)
	, status varchar(15) default 'ACTIVE' not null  -- 재직 상태 (ACTIVE / RESIGNED)
	, constraint fk_emp_department foreign key(department_id) references department(department_id)
	, constraint fk_emp_job foreign key(job_id) references job(job_id)
	, constraint fk_emp_hr_position foreign key(hr_position_id) references hr_position(hr_position_id)
	, constraint uq_emp_emp_no unique(emp_no)
	, constraint uq_emp_email unique(email)
) engine=InnoDB default charset=utf8mb4;

-- department.manager_id → emp 는 서로 참조하는 관계라 emp 생성 후 추가
alter table department add constraint fk_department_manager foreign key(manager_id) references emp(emp_id);

-- ----------------------------------------
-- 계정 : 시스템 로그인 계정. 사원 1명당 계정 1개. Role은 이 계정에 부여한다.
-- ----------------------------------------
create table account(
	account_id int primary key auto_increment        -- 계정 ID
	, emp_id int not null                            -- 사원 ID (1:1)
	, login_id varchar(25) not null                  -- 로그인 아이디
	, password varchar(255) not null                 -- BCrypt 비밀번호
	, enabled tinyint(1) default 1                   -- 사용 여부 (1/0) → isEnabled()
	, fail_count tinyint default 0                   -- 연속 로그인 실패 횟수
	, is_locked tinyint(1) default 0                 -- 잠금 여부 (1/0) → isAccountNonLocked()
	, locked_at datetime                             -- 잠금 일시
	, created_at datetime default current_timestamp  -- 계정 생성일
	, last_login_at datetime                         -- 마지막 로그인 일시
	, constraint fk_account_emp foreign key(emp_id) references emp(emp_id)
	, constraint uq_account_emp_id unique(emp_id)
	, constraint uq_account_login_id unique(login_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 계정별 역할 : 계정과 Role의 N:M 매핑. 한 계정에 여러 Role을 줄 수 있다.
-- ----------------------------------------
create table account_role(
	account_role_id int primary key auto_increment    -- 계정별 역할 ID
	, account_id int not null                         -- 계정 ID
	, hr_role_id int not null                         -- 역할 ID
	, constraint fk_account_role_account foreign key(account_id) references account(account_id)
	, constraint fk_account_role_hr_role foreign key(hr_role_id) references hr_role(hr_role_id)
	, constraint uq_account_role unique(account_id, hr_role_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 역할 부여 이력 : 계정에 Role을 부여/회수한 이력.
-- ----------------------------------------
create table account_role_history(
	account_role_history_id int primary key auto_increment    -- 이력 ID
	, account_id int not null                                 -- 대상 계정
	, hr_role_id int not null                                 -- 대상 역할
	, action_type varchar(10) not null                        -- 처리 구분 (GRANT / REVOKE)
	, reason varchar(200)                                     -- 사유
	, register_id int                                         -- 처리자 사원 ID
	, created_at datetime default current_timestamp           -- 처리 일시
	, constraint fk_account_role_history_account foreign key(account_id) references account(account_id)
	, constraint fk_account_role_history_hr_role foreign key(hr_role_id) references hr_role(hr_role_id)
	, constraint fk_account_role_history_register foreign key(register_id) references emp(emp_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 로그인 이력 : 로그인 성공/실패/잠금/로그아웃 이력.
-- ----------------------------------------
create table login_history(
	login_history_id int primary key auto_increment    -- 로그인 이력 ID
	, account_id int                                   -- 계정 ID (없는 아이디면 null)
	, login_id varchar(25) not null                    -- 시도한 아이디
	, result varchar(15) not null                      -- 결과 (SUCCESS / FAIL / LOCKED / LOGOUT)
	, ip_address varchar(45)                           -- 접속 IP
	, created_at datetime default current_timestamp    -- 발생 일시
	, constraint fk_login_history_account foreign key(account_id) references account(account_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 인사발령 : 입사·승진·전보·직책 임명·퇴직 발령 이력. 발령 전/후 부서·직급·직책을 남긴다.
-- ----------------------------------------
create table hr_appointment(
	hr_appointment_id int primary key auto_increment    -- 발령 ID
	, emp_id int not null                               -- 대상 사원
	, appointment_type varchar(20) not null             -- 발령 구분
	, before_department_id int                          -- 발령 전 부서
	, after_department_id int                           -- 발령 후 부서
	, before_job_id int                                 -- 발령 전 직급
	, after_job_id int                                  -- 발령 후 직급
	, before_hr_position_id int                         -- 발령 전 직책
	, after_hr_position_id int                          -- 발령 후 직책
	, effective_date date not null                      -- 발령일
	, reason varchar(200)                               -- 발령 사유
	, register_id int                                   -- 등록자 사원 ID
	, created_at datetime default current_timestamp     -- 등록 일시
	, constraint fk_hr_appointment_emp foreign key(emp_id) references emp(emp_id)
	, constraint fk_hr_appointment_before_department foreign key(before_department_id) references department(department_id)
	, constraint fk_hr_appointment_after_department foreign key(after_department_id) references department(department_id)
	, constraint fk_hr_appointment_before_job foreign key(before_job_id) references job(job_id)
	, constraint fk_hr_appointment_after_job foreign key(after_job_id) references job(job_id)
	, constraint fk_hr_appointment_before_hr_position foreign key(before_hr_position_id) references hr_position(hr_position_id)
	, constraint fk_hr_appointment_after_hr_position foreign key(after_hr_position_id) references hr_position(hr_position_id)
	, constraint fk_hr_appointment_register foreign key(register_id) references emp(emp_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 근태 : 사원별 일자별 출퇴근 기록. 하루 1건.
-- ----------------------------------------
create table attendance(
	attendance_id int primary key auto_increment    -- 근태 ID
	, emp_id int not null                           -- 사원 ID
	, work_date date not null                       -- 근무일
	, check_in_at datetime                          -- 출근 일시
	, check_out_at datetime                         -- 퇴근 일시
	, status varchar(15) not null                   -- 근태 상태
	, constraint fk_attendance_emp foreign key(emp_id) references emp(emp_id)
	, constraint uq_attendance unique(emp_id, work_date)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 근태 마감 : 월 단위 근태 마감. 행이 있으면 그 달은 마감된 것이다.
-- ----------------------------------------
create table attendance_close(
	attendance_close_id int primary key auto_increment    -- 마감 ID
	, close_month varchar(7) not null                     -- 마감 월 (YYYY-MM)
	, register_id int                                     -- 마감 처리자 사원 ID
	, closed_at datetime default current_timestamp        -- 마감 일시
	, constraint fk_attendance_close_register foreign key(register_id) references emp(emp_id)
	, constraint uq_attendance_close_close_month unique(close_month)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 근태 정정 신청 : 출퇴근 시각 정정 신청과 승인. 승인되면 attendance에 반영한다.
-- ----------------------------------------
create table attendance_correction(
	attendance_correction_id int primary key auto_increment    -- 정정 신청 ID
	, emp_id int not null                                      -- 신청 사원
	, work_date date not null                                  -- 정정 대상 근무일
	, request_check_in_at datetime                             -- 정정 요청 출근 일시
	, request_check_out_at datetime                            -- 정정 요청 퇴근 일시
	, reason varchar(200) not null                             -- 신청 사유
	, status varchar(15) default 'REQUESTED' not null          -- 처리 상태
	, request_date datetime default current_timestamp          -- 신청 일시
	, approver_id int                                          -- 승인자 사원 ID
	, approved_at datetime                                     -- 승인/반려 일시
	, reject_reason varchar(200)                               -- 반려 사유
	, constraint fk_attendance_correction_emp foreign key(emp_id) references emp(emp_id)
	, constraint fk_attendance_correction_approver foreign key(approver_id) references emp(emp_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 초과근무 신청 : 초과근무 신청과 승인.
-- ----------------------------------------
create table overtime_request(
	overtime_request_id int primary key auto_increment    -- 초과근무 신청 ID
	, emp_id int not null                                 -- 신청 사원
	, work_date date not null                             -- 근무일
	, start_at datetime not null                          -- 시작 일시
	, end_at datetime not null                            -- 종료 일시
	, overtime_hours decimal(4,1) not null                -- 초과근무 시간
	, reason varchar(200)                                 -- 신청 사유
	, status varchar(15) default 'REQUESTED' not null     -- 처리 상태
	, request_date datetime default current_timestamp     -- 신청 일시
	, approver_id int                                     -- 승인자 사원 ID
	, approved_at datetime                                -- 승인/반려 일시
	, reject_reason varchar(200)                          -- 반려 사유
	, constraint fk_overtime_request_emp foreign key(emp_id) references emp(emp_id)
	, constraint fk_overtime_request_approver foreign key(approver_id) references emp(emp_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 휴가 유형 : 연차·반차·병가 등 휴가 유형과 연차 차감 여부.
-- ----------------------------------------
create table leave_type(
	leave_type_id int primary key auto_increment    -- 휴가 유형 ID
	, leave_type_code varchar(20) not null          -- 유형 코드
	, leave_type_name varchar(30) not null          -- 유형명
	, is_deduct tinyint(1) default 1                -- 연차 차감 여부 (1/0)
	, is_active tinyint(1) default 1                -- 활성 여부 (1/0)
	, constraint uq_leave_type_leave_type_code unique(leave_type_code)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 연차 : 사원별 연도별 연차 부여·사용 일수. 잔여 = 부여 - 사용.
-- ----------------------------------------
create table annual_leave(
	annual_leave_id int primary key auto_increment    -- 연차 ID
	, emp_id int not null                             -- 사원 ID
	, leave_year int not null                         -- 부여 연도
	, granted_days decimal(4,1) default 0 not null    -- 부여 일수
	, used_days decimal(4,1) default 0 not null       -- 사용 일수
	, created_at datetime default current_timestamp   -- 등록 일시
	, constraint fk_annual_leave_emp foreign key(emp_id) references emp(emp_id)
	, constraint uq_annual_leave unique(emp_id, leave_year)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 휴가 신청 : 휴가 신청서. 결재 진행은 leave_approval에 단계별로 남는다.
-- ----------------------------------------
create table leave_request(
	leave_request_id int primary key auto_increment    -- 휴가 신청 ID
	, emp_id int not null                              -- 신청 사원
	, leave_type_id int not null                       -- 휴가 유형
	, start_date date not null                         -- 시작일
	, end_date date not null                           -- 종료일
	, days decimal(4,1) not null                       -- 사용 일수
	, reason varchar(200)                              -- 사유
	, status varchar(20) default 'REQUESTED' not null  -- 결재 상태
	, request_date datetime default current_timestamp  -- 신청 일시
	, constraint fk_leave_request_emp foreign key(emp_id) references emp(emp_id)
	, constraint fk_leave_request_leave_type foreign key(leave_type_id) references leave_type(leave_type_id)
) engine=InnoDB default charset=utf8mb4;

-- ----------------------------------------
-- 휴가 결재 : 휴가 신청의 1차·2차 결재 내역.
-- ----------------------------------------
create table leave_approval(
	leave_approval_id int primary key auto_increment    -- 결재 ID
	, leave_request_id int not null                     -- 휴가 신청 ID
	, approval_step tinyint(1) not null                 -- 결재 단계 (1 / 2)
	, approver_id int not null                          -- 결재자 사원 ID
	, status varchar(15) default 'WAITING' not null     -- 결재 상태
	, approval_comment varchar(200)                     -- 결재 의견 / 반려 사유
	, approved_at datetime                              -- 결재 일시
	, constraint fk_leave_approval_leave_request foreign key(leave_request_id) references leave_request(leave_request_id)
	, constraint fk_leave_approval_approver foreign key(approver_id) references emp(emp_id)
	, constraint uq_leave_approval unique(leave_request_id, approval_step)
) engine=InnoDB default charset=utf8mb4;
