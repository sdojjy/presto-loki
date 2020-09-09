# presto-loki
presto connector for loki

## Table 
map loki date to one table
```sql
presto> show create table loki.default.loki;
           Create Table           
----------------------------------
 CREATE TABLE loki.default.loki ( 
    labels Label,                 
    timestamp integer,            
    value varchar                 
 )   
```
## Query
use loki query_range and label predicate will be pushed down as a loki query parameter

example: 
```sql
select * from loki.default.loki where timestamp > 1599575057 and timestamp <1599575157 and labels = label('{job="varlogs"}');
```