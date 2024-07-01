# 数据库初始化

-- 创建库
create database if not exists task_db;

-- 切换库
use task_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;

-- 任务表
create table if not exists task
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint                                 not null comment '用户id',
    taskName   varchar(256) unique                    not null comment '任务名称',
    curStage   int          default 0                 not null comment '任务阶段',
    curStatus  varchar(256) default 'todo'            not null comment '当前阶段任务状态：todo/running/done/failed',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint      default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_taskName (taskName)
) comment '任务' collate = utf8mb4_unicode_ci;

-- Uncertainty参数配置表
create table if not exists uncertainty
(
    id             bigint auto_increment comment 'id' primary key,
    taskId         bigint                             not null comment '任务id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除',
    Num_S          varchar(256)                       not null comment '样本（模型）数量',
    TwrTH          varchar(256)                       not null comment '塔筒顶部高度/m',
    TwrBD          varchar(256)                       not null comment '塔筒底部外径/m',
    TwrBT          varchar(256)                       not null comment '塔筒底部厚度/m',
    TwrTD          varchar(256)                       not null comment '塔筒顶部外径/m',
    TwrTT          varchar(256)                       not null comment '塔筒顶部厚度/m',
    SubTH          varchar(256)                       not null comment '下部结构顶部（转换平台）高度/m',
    WatDep         varchar(256)                       not null comment '水深/m',
    SubUD          varchar(256)                       not null comment '下部结构一致外径/m',
    SubUT          varchar(256)                       not null comment '下部结构一致厚度/m',
    MatStrMean     varchar(256)                       not null comment '材料屈服强度/Pa的正态分布均值',
    MatStrVariance varchar(256)                       not null comment '材料屈服强度/Pa的正态分布方差',
    MatMod         varchar(256)                       not null comment '材料弹性模量/Pa',
    MatDen         varchar(256)                       not null comment '材料密度/kg/m^3',
    MatPoR         varchar(256)                       not null comment '材料泊松比',
    HubMass        varchar(256)                       not null comment '轮毂质量/kg',
    NacMass        varchar(256)                       not null comment '机舱质量/kg',
    HubH           varchar(256)                       not null comment '轮毂高度/m',
    BlDR           varchar(256)                       not null comment '叶片阻尼比/%',
    StDR           varchar(256)                       not null comment '结构阻尼比/%',
    WindSpd        varchar(256)                       not null comment '平均风速/m/s',
    WindDirMin     varchar(256)                       not null comment '风向/deg最小值',
    WindDirMax     varchar(256)                       not null comment '风向/deg最大值',
    TurbC          varchar(256)                       not null comment '湍流强度',
    Z0             varchar(256)                       not null comment '地面粗糙长度/m',
    WaveHs         varchar(256)                       not null comment '波浪极值波高/m',
    WaveTp         varchar(256)                       not null comment '波浪峰值周期/s',
    WaveDir        varchar(256)                       not null comment '波浪入流方向/deg',
    SeisPGA        varchar(256)                       not null comment '地震PGA/g',
    SeisDir        varchar(256)                       not null comment '地震入射方向/deg',
    index idx_taskId (taskId)
) comment 'Uncertainty参数配置' collate = utf8mb4_unicode_ci;

-- Condition参数配置表
create table if not exists `condition`
(
    id              bigint auto_increment comment 'id' primary key,
    taskId          bigint                             not null comment '任务id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    AnaTy           varchar(256)                       not null comment '分析方式 IDA = 1, 随机模拟= 2',
    NumF            varchar(256)                       not null comment '每个风速强度需模拟的随机风场数量',
    IMImpM          varchar(256)                       not null comment '风速强度输入方法',
    WindSpd         varchar(256)                       not null comment '轮毂风速强度/m/s',
    WPInpM          varchar(256)                       not null comment '波浪参数输入方法',
    WaveHsF         varchar(256)                       not null comment '波浪极值波高/m',
    WaveTpF         varchar(256)                       not null comment '波浪峰值周期/s',
    OM              varchar(256)                       not null comment '风机运行模式',
    ShutT           varchar(256)                       not null comment '制动停机开始时间/s',
    SimT            varchar(256)                       not null comment '每个工况模拟时间/s',
    OpenFastDT      varchar(256)                       not null comment 'OpenFAST分析步长/s',
    BldSimM         varchar(256)                       not null comment '叶片模拟方法',
    TurbModel       varchar(256)                       not null comment '湍流模型',
    IEC_WindType    varchar(256)                       not null comment 'IEC 湍流类型',
    WindProfileType varchar(256)                       not null comment '风剖面类型',
    PLExp           varchar(256)                       not null comment '风剖面幂指数',
    WakeMod         varchar(256)                       not null comment '尾流模型',
    IM_Model        varchar(256)                       not null comment 'OpenSees运行风速强度范围',
    IM_Nth          varchar(256)                       not null comment '从第N个强度开始OpenSees计算',
    DampR           varchar(256)                       not null comment '结构阻尼比',
    OpenSeesDt      varchar(256)                       not null comment 'OpenSees分析步长/s',
    index idx_taskId (taskId)
) comment 'Condition参数配置' collate = utf8mb4_unicode_ci;

-- Vulnerability参数配置表
create table if not exists vulnerability
(
    id          bigint auto_increment comment 'id' primary key,
    taskId      bigint                             not null comment '任务id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    Frgl_Op     varchar(256)                       not null comment '用于生成易脆性的计算结果',
    IniTI       varchar(256)                       not null comment '在计算幅值时忽略响应的前N秒/s',
    Frgl_Res    varchar(256)                       not null comment '用于生成易脆性的响应的方向性',
    Frgl_Bld    varchar(256)                       not null comment '是否计算叶片易脆性',
    DS_Bld      varchar(256)                       not null comment '叶片损伤状态（位移角）',
    IMI_Bld     varchar(256)                       not null comment '用于计算叶片易脆性的风速强度范围',
    Frgl_Nac    varchar(256)                       not null comment '是否计算机舱易脆性',
    DS_Nac      varchar(256)                       not null comment '机舱损伤状态（加速度）',
    IMI_Nac     varchar(256)                       not null comment '用于计算机舱易脆性的风速强度范围',
    Frgl_Twr    varchar(256)                       not null comment '是否计算塔筒易脆性',
    DS_Twr_Type varchar(256)                       not null comment '塔筒损伤类型',
    DS_Twr      varchar(256)                       not null comment '塔筒损伤状态',
    IMI_Twr     varchar(256)                       not null comment '用于计算塔筒易脆性的风速强度范围',
    Frgl_Sub    varchar(256)                       not null comment '是否计算下部结构易脆性',
    DS_Sub_Type varchar(256)                       not null comment '下部结构损伤类型',
    DS_Sub      varchar(256)                       not null comment '下部结构损伤状态',
    IMI_Sub     varchar(256)                       not null comment '用于计算下部结构易脆性的风速强度范围',
    HIF         varchar(256)                       not null comment '易脆性曲线风速强度范围',
    dIM         varchar(256)                       not null comment '易脆性曲线步长/s',
    Num_RS      varchar(256)                       not null comment '每个风速强度的MCS数量',
    LossR_Bld   varchar(256)                       not null comment '叶片损伤状态相应的损失比',
    LossR_Nac   varchar(256)                       not null comment '机舱损伤状态相应的损失比',
    LossR_Twr   varchar(256)                       not null comment '塔筒损伤状态相应的损失比',
    LossR_Mop   varchar(256)                       not null comment '单桩损伤状态相应的损失比',
    Dep_Bld     varchar(256)                       not null comment '扫塔时塔筒和叶片损伤状态调为最严重',
    Dep_Nac     varchar(256)                       not null comment '机舱发生最严重损伤时叶片状态调为最严重',
    Dep_Twr     varchar(256)                       not null comment '塔筒发生最严重损伤时叶片和机舱状态调为最严重',
    Dep_Mop     varchar(256)                       not null comment '单桩发生最严重损伤时叶片，机舱和塔筒状态调为最严重',
    index idx_taskId (taskId)
) comment 'Vulnerability参数配置' collate = utf8mb4_unicode_ci;