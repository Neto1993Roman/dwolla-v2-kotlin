package com.dwolla.util

import com.dwolla.Client
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class TokenManagerTest {
    val existingAccessToken = "existing-access-token"
    val newAccessToken = "new-access-token"

    @Test
    fun `TokenManager#getAccessToken gets and sets new access token if null`() {
        val client = mockClient()
        val tokenManager = TokenManager(client)

        assertEquals(newAccessToken, tokenManager.getAccessToken())
    }

    @Test
    fun `TokenManager#getAccessToken returns existing token if fresh`() {
        val client = mockClient()
        val tokenManager = TokenManager(client)
        tokenManager.token = mockToken(fresh = true)

        assertEquals(existingAccessToken, tokenManager.getAccessToken())
    }

    @Test
    fun `TokenManager#getAccessToken replaces existing token if expired`() {
        val client = mockClient()
        val tokenManager = TokenManager(client)
        tokenManager.token = mockToken(fresh = false)

        assertEquals(newAccessToken, tokenManager.getAccessToken())
    }

    private fun mockClient(): Client {
        val client = mockk<Client>()
        every { client.fetchToken() } returns Token(newAccessToken, 3600)
        return client
    }

    private fun mockToken(fresh: Boolean): Token {
        val token = mockk<Token>()
        every { token.accessToken } returns existingAccessToken
        every { token.isExpired() } returns !fresh
        return token
    }
}
