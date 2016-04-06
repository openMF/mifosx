Mifos X: A Platform for Microfinance
======

Mifos X is an open technology platform for financial inclusion that provides the core functionality needed to deliver financial services to the 2 billion poor and unbanked. Mifos X can be deployed in any environment: cloud or on-premise, on or offline, mobile or PC; it’s extensible enough to support any organizational type or delivery channel, and flexible enough to support any product, service, or methodology. For any organization, big or small, it will provide the client data management, loan and savings portfolio management, integrated real time accounting and social and financial reporting need to bring digital financial services in a modern connected world.It contains both the common platform for our community to build off and extend, and the Community App, our showcase product which you can use out of the box. Both are completely open via the Mozilla Public License 2.0.


Build Status
============

Travis

[![Build
Status](https://travis-ci.org/openMF/mifosx.png?branch=master)](https://travis-ci.org/openMF/mifosx)

Cloudbees Jenkins

[![Build
Status](https://openmf.ci.cloudbees.com/job/MIFOSX%20INTEGRATION%20TEST/badge/icon)](https://openmf.ci.cloudbees.com/job/MIFOSX%20INTEGRATION%20TEST/)

<a target="_blank" href="https://openmf.ci.cloudbees.com/job/MIFOSX%20INTEGRATION%20TEST/"  title="Jenkins@CloudBees">Jenkins@CloudBees Unit + Integration Tests</a>


Version
==========

The API for the mifos-platform (project named 'Mifos X')is documented in the api-docs under <b>Full API Matrix</b> and can be viewed <a target="_blank" href="https://demo.openmf.org/api-docs/apiLive.htm" title="API Documentation"> here
</a>

Latest stable release can always been viewed on master branch: <a target="_blank" href="https://github.com/openMF/mifosx/tree/master" title="Latest Release">Latest Release on Master</a>, <a target="_blank" href="https://github.com/openMF/mifosx/blob/master/CHANGELOG.md" title="Latest release change log">View change log</a>

License
=============

This project is licensed under the open source MPL V2. See https://github.com/openMF/mifosx/blob/master/LICENSE.md

Mifos Platform API
=====================

<a target="_blank" href="https://demo.openmf.org/api-docs/apiLive.htm" title="mifos platform api">API Documentation (Demo Server)</a>


Online Demos
=============================

* <a target="_blank" href="https://demo.openmf.org" title="Reference Client App">Community App</a>
* ~~<a target="_blank" href="https://demo.openmf.org/old/" title="Community App">Reference Client App (Deprecated)</a>~~

Developers
==========
see https://mifosforge.jira.com/wiki/display/MIFOSX/MifosX+Technical - Developers Wiki Page

see https://mifosforge.jira.com/wiki/display/MIFOSX/Getting+started+-+Contributing+to+MifosX  - Getting Started.

see https://mifosforge.jira.com/wiki/display/MIFOSX/The+Basic+Design - Overview of Platform Implementation

see https://github.com/openMF/mifosx/wiki/Screen-Based-Reporting for info around reporting

see https://github.com/openMF/mifosx/wiki/Git-Usuage for info around using git

see https://www.ohloh.net/p/mifosx for activity overview and basic code analysis.

Roadmap
==============

<a target="_blank" href="http://goo.gl/IXS9Q" title="Community Roadmap (High Level)">Community Roadmap (High Level)</a>

<a target="_blank" href="https://mifosforge.jira.com/browse/MIFOSX#selectedTab=com.atlassian.jira.plugin.system.project%3Aroadmap-panel" 
   title="Project Release Roadmap on JIRA (Detailed View)">Project Release Roadmap on JIRA (Detailed View)</a>

Video Demonstration
===============

Demonstration of first Prototype of this platform with browser App (April 2012) - http://www.youtube.com/watch?v=zN5Dn1Lc_js

Installation using Dockerfiles
==============================

Linux kernel allows running multiple isolated user space instances. A container is such an isolated environment where one or more processes can be run. Containers focus on process isolation and containment instead of emulating a complete physical machine. Historically, chroot in Linux kernel has provided some level of isolation by providing an environment to create and host a virtualized copy of a software system, ever since early ‘80s. But the term process containers didn’t come up until around late 2006. It was soon renamed to ’Control Groups’ (cgroups) to avoid confusion caused by multiple meanings of the term 'containers’ in the Linux kernel. Control Groups is a linux kernel feature available since v2.6.24 that limits and isolates the resource usage of a collection of processes. Subsequently, namespace isolation was added.This lead to the evolution of Linux Containers (LXC), an operating system level virtualization environment that is built on Linux kernel features mentioned earlier, like chroot, cgroups, namespace isolation, etc.


Given the recent massive spike in interest in Linux Containers, you could be forgiven for wondering, “Why now?”. It has been argued that the increasingly prevalent cloud computing model more closely resembles hosting providers than traditional enterprise IT, and that containers are a perfect match for this model.

Despite the sudden ubiquity of container technology, like so much in the world of open source software, containerization depends on a long series of previous innovations, especially in the operating system. “One cannot resist an idea whose time has come.” Containers are such an idea, one that has been a long time coming.

It's important to reiterate that the next evolution of Fineract Platform focuses on being faster, lighter and cheaper to change (than existing mifos) so that it is more responsive to the needs of Microfinance Institutions and Integrators.One of the ways of increasing agility for all stakeholder is to improve the ability of Fineract platform to deployed faster on Containers and other operating systems.


In the past two years, there has been rapid growth in both interest in and usage of container-based solutions. Almost all major IT vendors and cloud providers have announced container-based solutions, and there has been a proliferation of start-ups founded in this area as well.The rapid growth of the Docker project has served to make the Docker image format a de facto standard for many purposes.


Not bound to higher level constructs such as a particular client or orchestration stack
Not tightly associated with any particular commercial vendor or project
Portable across a wide variety of operating systems, hardware, CPU architectures, public clouds, etc.

Docker can build images automatically by reading the instructions from a Dockerfile. A Dockerfile is a text document that contains all the commands a user could call on the command line to assemble an image. Using docker build users can create an automated build that executes several command-line instructions in succession.

The docker build command builds an image from a Dockerfile and acontext. The build’s context is the files at a specified location PATH or URL. The PATH is a directory on your local filesystem. The URL is a the location of a Git repository.A context is processed recursively. So, a PATH includes any subdirectories and the URL includes the repository and its submodules. A simple build command that uses the current directory as context.

Docker containers are created by using [base] images. An image can be basic, with nothing but the operating-system fundamentals, or it can consist of a sophisticated pre-built application stack ready for launch.When building your images with docker, each action taken (i.e. a command executed such as apt-get install) forms a new layer on top of the previous one. These base images then can be used to create new containers.
In this Epic , our aim is to create dockerfiles that will help MFIs and integrators automate the installation of MifosX on different Operating Systems using docker.
MifosX is cuurently looking for contributors who have knowledge in UNIX containers , docker and Kubernetis.This will enable 
A Dockerfile automates the steps for crafting a Docker Image. Dockerfiles are incredibly powerful but require some extra work to define your exact application runtime environment. Currently ,there exists a Dockerfile Project which maintains a central repository of Dockerfile for various popular open source software services runnable on a Docker container.At the moment MifosX is missing and we would love to have our official docker repositories..This will make it convenient for any developer to download pre-built Docker images and rapidly develop, test, and deploy awesome applications with MifosX among others. All automated builds will be published to the public Docker Hub Registry .They will be automatically built directly from the repositories hosted on Github, and they are kept in sync and protected by Docker Hub Registry.

