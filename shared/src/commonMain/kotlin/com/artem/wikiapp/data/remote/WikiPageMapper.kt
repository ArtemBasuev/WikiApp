package com.artem.wikiapp.data.remote

import com.artem.wikiapp.data.Category
import com.artem.wikiapp.data.PageLink
import com.artem.wikiapp.data.WikiPage
import com.artem.wikiapp.data.remote.dto.WikiPageDto

fun WikiPageDto.toWikiPage(): WikiPage = WikiPage(
    pageid = pageid,
    title = title,
    extract = extract,
    fullurl = fullurl,
    length = length,
    touched = touched,
    categories = categories.map { Category(it.title) },
    links = links.map { PageLink(it.title) },
)
