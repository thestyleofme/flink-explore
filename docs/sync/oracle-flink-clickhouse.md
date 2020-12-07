# 使用kafka-connector-oracle实时同步数据到clickhouse

## 1. 准备

### 1.1 oracle 

```sql
-- ----------------------------
-- Table structure for test1
-- ----------------------------
DROP TABLE "C##KMINER"."test1";
CREATE TABLE "C##KMINER"."test1" (
  "id" NVARCHAR2(50) VISIBLE NOT NULL ,
  "name" VARCHAR2(50 BYTE) VISIBLE ,
  "sex" VARCHAR2(10 BYTE) VISIBLE ,
  "last_update_date" TIMESTAMP(6) VISIBLE DEFAULT CURRENT_TIMESTAMP  
)
TABLESPACE "USERS"
LOGGING
NOCOMPRESS
PCTFREE 10
INITRANS 1
STORAGE (
  INITIAL 65536 
  NEXT 1048576 
  MINEXTENTS 1
  MAXEXTENTS 2147483645
  BUFFER_POOL DEFAULT
)
PARALLEL 1
NOCACHE
DISABLE ROW MOVEMENT
;

-- ----------------------------
-- Primary Key structure for table test1
-- ----------------------------
ALTER TABLE "C##KMINER"."test1" ADD CONSTRAINT "SYS_C0014018" PRIMARY KEY ("id");
```
### 1.2  kafka connector oracle

[kafka-connect-oracle配置详情](https://github.com/thestyleofme/kafka-connect-oracle.git)

```properties
name=oracle-connector-test
connector.class=com.ecer.kafka.connect.oracle.OracleSourceConnector
db.name.alias=test
tasks.max=1
topic=kafka_oracle_test
db.name=ORCLCDB
db.hostname=172.23.16.75
db.port=21521
db.user=C##KMINER
db.user.password=123456
db.fetch.size=1
table.whitelist=C##KMINER.*
table.blacklist=C##TEST.*
parse.dml.data=true
reset.offset=false
start.scn=
multitenant=true
```
查看connector是否正确启动

```http request
curl -s hdspdemo003:8083/connectors/oracle-connector-test/status
```

### 1.3  flink

flink运行配置文件，```src/main/resources/kafka-connector-sync-clickhouse-jdbc.json```
```json
{
  "syncFlink": {
    "jobName": "flink_oracle_realtime_sync",
    "sourceSchema": "C##KMINER",
    "sourceTable": "test1",
    "checkPointPath": "file:///E:/myGitCode/flink-explore/flink-checkpoints/",
    "writeType": "jdbc"
  },
  "sourceKafka": {
    "kafkaBootstrapServers": "hdspdemo003:6667",
    "kafkaTopic": "kafka_oracle_test",
    "initDefaultOffset": "latest",
    "sourceFrom": "ORACLE_KAFKA_CONNECTOR"
  },
  "syncJdbc": {
    "dbType": "CLICKHOUSE",
    "driver": "ru.yandex.clickhouse.ClickHouseDriver",
    "jdbcUrl": "jdbc:clickhouse://172.23.16.68:8123/hdsp",
    "schema": "hdsp",
    "table": "test1",
    "batchInterval": 1,
    "cols": [
      {
        "colName": "id",
        "colType": "STRING"
      },
      {
        "colName": "name",
        "colType": "STRING"
      },
      {
        "colName": "sex",
        "colType": "STRING"
      },
      {
        "colName": "last_update_date",
        "colType": "DATE"
      }
    ],
    "pk": "id",
    "insert": {
      "query": "insert into test1(id,name,sex,last_update_date) values (?,?,?,?)",
      "colTypes": "VARCHAR, VARCHAR, VARCHAR, VARCHAR"
    },
    "update": {
      "query": "insert into test1(id,name,sex,last_update_date) values (?,?,?,?)",
      "colTypes": "VARCHAR, VARCHAR, VARCHAR, VARCHAR"
    },
    "delete": {
      "query": "insert into test1(id,name,sex,last_update_date) values (?,?,?,?)",
      "colTypes": "VARCHAR, VARCHAR, VARCHAR, VARCHAR"
    }
  }
}
```

### 1.4  clickhouse

ck引擎推荐使用ReplacingMergeTree，这样可以按照配置的version去除大量历史数据，如下面使用的```last_update_date```

```sql
drop table hdsp.test1;

create table hdsp.test1(
    id UInt32,
    name String,
    sex String,
	last_update_date DateTime('Asia/Shanghai')
)
engine=ReplacingMergeTree(last_update_date)
order by id;

select * from hdsp.test1;

OPTIMIZE TABLE hdsp.test1;
```

## 3. 运行flink

可本地idea运行，也可打包```mvn clean package -DskipTests```，将jar包在flink集群运行