package io.github.ajaypal.swipeablelist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SwipeableListInternalsTest {

    @Test
    fun `buildScrollIndexByItemKey returns flat list indexes`() {
        val result = buildScrollIndexByItemKey<String, Any>(
            flatItems = listOf("Alice", "Ben", "Cara"),
            groupsMap = null,
            flatKey = { it },
        )

        assertEquals(mapOf("Alice" to 0, "Ben" to 1, "Cara" to 2), result)
    }

    @Test
    fun `buildVisibleFlatItems excludes removing keys`() {
        val result = buildVisibleFlatItems(
            flatItems = listOf("Alice", "Ben", "Cara"),
            removingKeys = setOf("Ben"),
            flatKey = { it },
        )

        assertEquals(listOf("Alice", "Cara"), result)
    }

    @Test
    fun `buildScrollIndexByItemKey offsets grouped items by headers`() {
        val result = buildScrollIndexByItemKey(
            flatItems = null,
            groupsMap = linkedMapOf(
                'A' to listOf("Alice", "Aaron"),
                'B' to listOf("Ben"),
            ),
            flatKey = { it },
        )

        assertEquals(mapOf("Alice" to 1, "Aaron" to 2, "Ben" to 4), result)
    }

    @Test
    fun `buildVisibleGroupsMap removes headers with no visible items`() {
        val result = buildVisibleGroupsMap(
            groupsMap = linkedMapOf(
                'A' to listOf("Alice"),
                'B' to listOf("Ben", "Bella"),
            ),
            removingKeys = setOf("Alice", "Ben"),
            flatKey = { it },
        )

        assertEquals(linkedMapOf('B' to listOf("Bella")), result)
    }

    @Test
    fun `buildScrollIndexByItemKey keeps grouped item targets correct across many headers`() {
        val groupedPlaces = sortedMapOf(
            'A' to listOf("Adelaide", "Alice Springs"),
            'B' to listOf("Ballarat", "Brisbane", "Broome"),
            'C' to listOf("Cairns"),
            'D' to listOf("Darwin"),
            'G' to listOf("Geelong", "Gold Coast"),
            'H' to listOf("Hobart"),
            'L' to listOf("Launceston"),
            'M' to listOf("Melbourne"),
            'N' to listOf("Newcastle"),
            'P' to listOf("Perth"),
            'S' to listOf("Sydney"),
            'T' to listOf("Townsville"),
            'W' to listOf("Wollongong"),
        )

        val result = buildScrollIndexByItemKey(
            flatItems = null,
            groupsMap = groupedPlaces,
            flatKey = { it.lowercase().replace(" ", "-") },
        )

        assertEquals(23, result.getValue("perth"))
        assertEquals(29, result.getValue("wollongong"))
    }

    @Test
    fun `lazy keys are namespaced by entry type`() {
        assertNotEquals(headerLazyKey("same"), itemLazyKey("same"))
    }

    @Test
    fun `settleTargetOffset opens left panel when reveal passes threshold`() {
        val result = settleTargetOffset(
            currentOffsetX = 120f,
            previousOffsetX = 0f,
            leftPanelWidth = 180f,
            rightPanelWidth = 80f,
            swipeThresholdPx = 100f,
        )

        assertEquals(180f, result)
    }

    @Test
    fun `settleTargetOffset closes row when hiding passes threshold`() {
        val result = settleTargetOffset(
            currentOffsetX = 20f,
            previousOffsetX = 150f,
            leftPanelWidth = 180f,
            rightPanelWidth = 80f,
            swipeThresholdPx = 100f,
        )

        assertEquals(0f, result)
    }

    @Test
    fun `settleTargetOffset can open narrow action panels`() {
        val result = settleTargetOffset(
            currentOffsetX = -40f,
            previousOffsetX = 0f,
            leftPanelWidth = 80f,
            rightPanelWidth = 60f,
            swipeThresholdPx = 100f,
        )

        assertEquals(-60f, result)
        assertEquals(30f, revealThresholdPx(100f, 60f))
    }

    @Test
    fun `settleTargetOffset closes open row on slight reverse drag`() {
        val result = settleTargetOffset(
            currentOffsetX = -58f,
            previousOffsetX = -60f,
            leftPanelWidth = 80f,
            rightPanelWidth = 60f,
            swipeThresholdPx = 100f,
        )

        assertEquals(0f, result)
    }
}
