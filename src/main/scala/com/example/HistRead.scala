/**
 *
 * Copyright (c) 2019 Cisco and/or its affiliates.
 *
 * This software is licensed to you under the terms of the Cisco Sample
 * Code License, Version 1.1 (the "License"). You may obtain a copy of the
 * License at
 *
 *                https://developer.cisco.com/docs/licenses
 *
 * All use of the material herein must be in accordance with the terms of
 * the License. All rights not expressly granted by the License are
 * reserved. Unless required by applicable law or agreed to separately in
 * writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.example

import java.time._
import java.time.format.DateTimeFormatter

import org.dsa.iot.dslink.node.Permission
import org.dsa.iot.dslink.node.actions.table.Row
import org.dsa.iot.dslink.node.actions.{Action, ActionResult, Parameter}
import org.dsa.iot.dslink.node.value.{Value, ValueType}
import org.dsa.iot.dslink.{DSLink, DSLinkFactory, DSLinkHandler}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.slf4j.LoggerFactory

import scala.sys.process._

object HistRead {
  def main(args: Array[String]): Unit = {
    DSLinkFactory.start(args.drop(1), new HistReadDSLinkHandler(args(0)))
  }
}


class HistReadDSLinkHandler(histReadCmd: String) extends DSLinkHandler {
  private val log = LoggerFactory.getLogger(getClass)
  override val isResponder = true

  override def onResponderInitialized(link: DSLink): Unit = {
    val superRoot = link.getNodeManager.getSuperRoot
    superRoot
      .createChild("histRead", true)
      .setDisplayName("History Read")
      .setAction(new Action(Permission.READ, (event: ActionResult) => {
        val url = event.getParameter("URL").getString
        val path = event.getParameter("Path").getString
        val outFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
        val start = ZonedDateTime.of(LocalDateTime.parse(event.getParameter("Start").getString), ZoneId.systemDefault)
          .withZoneSameInstant(ZoneId.of("Z")).format(outFormatter)
        val end = ZonedDateTime.of(LocalDateTime.parse(event.getParameter("End").getString), ZoneId.systemDefault)
          .withZoneSameInstant(ZoneId.of("Z")).format(outFormatter)
        val cmd = Seq(histReadCmd, "-l", "0", "-u", url, "-p", path, "--starttime", start, "--endtime", end)
        val splitLines = augmentString(cmd.!!).lines.toList.drop(3).map(_.split(" +"))
        val inFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss[.SSSSSS]").withZone(ZoneId.of("Z"))
        val json = splitLines.map { a =>
          val timestamp = ZonedDateTime.parse(a(0) + " " + a(1), inFormatter).format(DateTimeFormatter.ISO_INSTANT)
          ("timestamp" -> timestamp) ~ ("value" -> a(3))
        }
        event.getTable.addRow(Row.make(new Value(true), new Value(compact(render(json)))))
      })
        .addParameter(new Parameter("URL", ValueType.STRING).setDescription("OPC UA End Point"))
        .addParameter(new Parameter("Path", ValueType.STRING).setDescription("Object Path"))
        .addParameter(new Parameter("Start", ValueType.STRING).setDescription("Start date time"))
        .addParameter(new Parameter("End", ValueType.STRING).setDescription("End data time"))
        .addResult(new Parameter("Success", ValueType.BOOL))
        .addResult(new Parameter("JSON", ValueType.STRING))
      )
      .build()

    log.info("HistRead initialized")
  }

  override def onResponderConnected(link: DSLink): Unit = {
    log.info("HistRead connected")
  }
}
