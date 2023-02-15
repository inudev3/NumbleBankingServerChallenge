package com.example.numblebankingserverchallenge.repository.member

import com.example.numblebankingserverchallenge.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.UUID

interface MemberRepository:JpaRepository<Member,UUID>, MemberRepositoryCustom,QuerydslPredicateExecutor<Member> {

    fun findByUsername(username:String):Member?
    fun findByUsernameAndEncryptedPassword(username: String, encrypted:String):Member?
}