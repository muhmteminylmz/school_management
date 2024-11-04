package com.schoolmanagment.repository;


import com.schoolmanagment.entity.concretes.EducationTerm;
import com.schoolmanagment.entity.enums.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EducationTermRepository extends JpaRepository<EducationTerm,Long> {


    @Query("SELECT (count(e)>0 ) FROM EducationTerm e where e.term = ?1 AND EXTRACT(YEAR FROM e.startDate) = ?2")
    //EXTRACT() Bizim gun ay yil gibi bircok seyin oldugu parametreyi ayirmamiza yarar(burda yili ayikladik)
    boolean existsByTermAndYearEquals(Term term, int year);

    @Query("SELECT (count(e)>0 ) FROM EducationTerm e WHERE e.id = ?1")
    boolean existsByIdEquals(Long id);

    @Query("SELECT e FROM EducationTerm e WHERE e.id = ?1")
    EducationTerm findByIdEquals(Long id);
}