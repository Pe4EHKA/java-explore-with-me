package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Hit;
import ru.practicum.model.HitStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.model.HitStats(hit.app, hit.uri, count(hit.ip)) " +
            "from Hit hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "and (hit.uri in ?3 or ?3 is null ) " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc ")
    List<HitStats> findHitStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.model.HitStats(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from Hit hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "and (hit.uri in ?3 or ?3 is null)" +
            "group by hit.app, hit.uri " +
            "order by count(distinct hit.ip) desc ")
    List<HitStats> findUniqueHitStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
