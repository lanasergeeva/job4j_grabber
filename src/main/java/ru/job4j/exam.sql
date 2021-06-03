select * from person;
select * from company;

select p.name, c.name
from person p
join company c
on(c.id=p.company_id)
where p.company_id != 5;

select c.name, count(p.name) cp
from company c 
join person p
on(c.id=p.company_id)
group by c.name
having count(p.name) = 
(select count(company_id) cc
from  person p
group by company_id
order by cc desc
limit 1);
