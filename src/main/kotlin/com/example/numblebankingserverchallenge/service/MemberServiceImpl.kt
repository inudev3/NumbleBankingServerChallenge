package com.example.numblebankingserverchallenge.service

import com.example.numblebankingserverchallenge.domain.Friendship
import com.example.numblebankingserverchallenge.domain.Member
import com.example.numblebankingserverchallenge.dto.FriendDTO

import com.example.numblebankingserverchallenge.dto.LoginVO
import com.example.numblebankingserverchallenge.dto.SignUpVO

import com.example.numblebankingserverchallenge.dto.UserDTO
import com.example.numblebankingserverchallenge.exception.UserExistsException
import com.example.numblebankingserverchallenge.exception.UserNotFoundException
import com.example.numblebankingserverchallenge.repository.FriendshipRepository
import com.example.numblebankingserverchallenge.repository.MemberRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MemberServiceImpl(private val memberRepository: MemberRepository, private val friendshipRepository: FriendshipRepository,private val passwordEncoder: PasswordEncoder) :
    MemberService {
    override fun findByUsername(username: String): UserDTO? =
        memberRepository.findByUsername(username)?.let { UserDTO(it.id, it.username) }


    override fun createUser(signUpVO: SignUpVO): UserDTO? {
        return memberRepository.findByUsername(signUpVO.username)?.let { return null } ?: run {
            val encrypted = passwordEncoder.encode(signUpVO.pw)
            val member = Member(signUpVO.username, encrypted).let { memberRepository.save(it) }
            return UserDTO(member.id, member.username)
        }
    }

    override fun login(loginVO: LoginVO): UserDTO? {
        val encoded = passwordEncoder.encode(loginVO.pw)
        return memberRepository.findByUsernameAndEncryptedPassword(loginVO.username, encoded)
            ?.let { UserDTO(it.id, it.username) }
    }

    override fun getFriends(id: UUID): List<UserDTO>? {
        val findMember = memberRepository.findById(id).orElseGet { null }?: return null
        return memberRepository.getFriends(findMember.id).map { UserDTO(it.id,it.username) }
    }

    override fun addFriend(userId:UUID,friendName: String): FriendDTO? {
        val findMember = memberRepository.findById(userId).orElse(null) ?:return null
        val findFriend = memberRepository.findByUsername(friendName) ?: return null
        val friendShip =Friendship(findMember, findFriend)
        val friendShip2 = Friendship(findFriend,findMember)
        friendshipRepository.save(friendShip2)
        return FriendDTO(friendshipRepository.save(friendShip).id,findMember.username,findFriend.username)
    }
}