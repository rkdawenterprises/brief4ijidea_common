@file:Suppress("ClassName",
               "PropertyName")

package net.ddns.rkdawenterprises.brief4ijidea.plugin.pages

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.*
import com.intellij.remoterobot.search.locators.byXpath
import java.time.Duration

fun RemoteRobot.welcomeFrame(function: Welcome_frame.() -> Unit)
{
    find(Welcome_frame::class.java,
         Duration.ofSeconds(10)).apply(function)
}

@FixtureName("Welcome Frame")
@DefaultXpath("type",
              "//div[@class='FlatWelcomeFrame']")
class Welcome_frame(remote_robot: RemoteRobot,
                    remote_component: RemoteComponent) : CommonContainerFixture(remote_robot,
                                                                                remote_component)
{
    val create_new_project_link
        get() = actionLink(byXpath("New Project",
                                   "//div[(@class='MainButton' and @text='New Project') or (@accessiblename='New Project' and @class='JButton')]"))
}