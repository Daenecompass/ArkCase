<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:detail>
<jsp:attribute name="endOfHead">
    <title><spring:message code="document.page.title" text="Document | ACM | Ark Case Management" /></title>
    <div id="detailData" itemscope="true" style="display: none">
        <span itemprop="objType">FILE</span>
        <span itemprop="objId">${objId}</span>
    </div>
</jsp:attribute>

<jsp:attribute name="endOfBody">
    <script type="text/javascript" src="<c:url value='/resources/js/document/document.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/document/documentModel.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/document/documentView.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/document/documentController.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/resources/js/document/documentService.js'/>"></script>


    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_slimscroll}/${js_slimscroll}'/>"></script>

    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_jtable}/blue/jtable.css'/>" type="text/css"/>
    <script type="text/javascript" src="<c:url value='/resources/vendors/${vd_jtable}/${js_jtable}'/>"></script>


    <link rel="stylesheet" href="<c:url value='/resources/vendors/${vd_acm}/themes/basic/${vd_x_editable}/css/bootstrap-editable.css'/>" type="text/css"/>
    <script src="<c:url value='/resources/vendors/${vd_x_editable}/js/${js_x_editable}'/>"></script>

</jsp:attribute>

<jsp:body>
<section id="content">
<header class="header bg-white b-b clearfix">
    <div class="row m-t-sm">
        <div class="col-sm-12 m-b-xs">
            <div class="pull-right inline">
                <div class="btn-group">

                    <button class="btn btn-default  btn-sm" data-toggle="modal" data-target="#replaceFile">

                        <span class="text">Replace File</span>
                    </button> <div class="modal fade" id="replaceFile" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="myModalLabel">Replace File</h4>
                            </div>
                            <div class="modal-body">


                                <p>Choose a file from your computer to replace [document name]:</p>

                                <label for="fileName">File</label><br/>
                                <input type="file" id="fileName" class="input-lg" />

                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                <button type="button" class="btn btn-primary">Replace File</button>
                            </div>
                        </div>
                    </div>
                </div>



                    <button class="btn btn-default  btn-sm" data-toggle="modal" data-target="#delete">

                        <span class="text">Delete</span>
                    </button> <div class="modal fade" id="delete" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="myModalLabel">Delete</h4>
                            </div>
                            <div class="modal-body">

                                <p>Are you sure you want to delete [file name] from [partent folder]?</p>


                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                <button type="button" class="btn btn-primary">Delete</button>
                            </div>
                        </div>
                    </div>
                </div>



                    <button class="btn btn-default  btn-sm" data-toggle="modal" data-target="#copy">

                        <span class="text">Copy</span>
                    </button> <div class="modal fade" id="copy" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="myModalLabel">Copy</h4>
                            </div>
                            <div class="modal-body">

                                <p>Where would you like to copy this file? Choose the directory from the box below:</p>
                                <p>[place tree view here]</p>

                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                <button type="button" class="btn btn-primary">Copy</button>
                            </div>
                        </div>
                    </div>
                </div>


                    <button class="btn btn-default  btn-sm" data-toggle="modal" data-target="#move">

                        <span class="text">Move</span>
                    </button> <div class="modal fade" id="move" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                <h4 class="modal-title" id="myModalLabel">Move</h4>
                            </div>
                            <div class="modal-body">

                                <p>Where would you like to move this file? Choose the directory from the box below:</p>
                                <p>[place tree view here]</p>

                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                <button type="button" class="btn btn-primary">Move</button>
                            </div>
                        </div>
                    </div>
                </div>



                    <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="Close Document" onClick="window.close();"><i class="fa fa-times"></i></button>
                </div>
            </div>
            <h4 class="m-n"> <a href="#" id="caseTitle" data-type="text" data-pk="1" data-url="/post" data-title="Enter Case Title"> Sample Document Title</a> (12321)</h4>
        </div>
    </div>
</header>
<section class="hbox stretch">
<aside class="aside-xxl bg-light dker b-r" id="subNav">
    <section class="scrollable">
        <div class="wrapper">
            <section class="panel panel-default portlet-item">
                <header class="panel-heading">
                    <ul class="nav nav-pills pull-right">
                        <li><div class="btn-group padder-v2"><button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Partcipant"><i class="fa fa-user"></i> New</button></div></li>
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    Participants <span class="badge bg-info">4</span> </header>
                <ul class="list-group alt panel-body">
                    <li class="list-group-item">
                        <div class="media"> <span class="pull-left thumb-sm"><img src="resources/images/a1.png" alt="John said" class="img-circle"></span>
                            <div class="btn-group pull-right">
                                <button type="button" class="dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-cog"></i> </button>
                                <ul class="dropdown-menu">
                                    <li><a href="#">Remove</a></li>
                                    <li><a href="#">Change Role</a></li>
                                </ul>
                            </div>
                            <div class="media-body">
                                <div><a href="#">David Miller</a></div>
                                <small class="text-muted">Author</small> </div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="media"> <span class="pull-left thumb-sm"><img src="resources/images/a2.png" alt="John said" class="img-circle"></span>
                            <div class="btn-group pull-right">
                                <button type="button" class="dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-cog"></i> </button>
                                <ul class="dropdown-menu">
                                    <li><a href="#">Remove</a></li>
                                    <li><a href="#">Change Role</a></li>
                                </ul>
                            </div>
                            <div class="media-body">
                                <div><a href="#">Matthew Maines</a></div>
                                <small class="text-muted">Co-Author</small> </div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="media"> <span class="pull-left thumb-sm"><img src="resources/images/a3.png" alt="John said" class="img-circle"></span>
                            <div class="btn-group pull-right">
                                <button type="button" class="dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-cog"></i> </button>
                                <ul class="dropdown-menu">
                                    <li><a href="#">Remove</a></li>
                                    <li><a href="#">Change Role</a></li>
                                </ul>
                            </div>
                            <div class="media-body">
                                <div><a href="#">Ronda Ringo</a></div>
                                <small class="text-muted">Contributor</small> </div>
                        </div>
                    </li>
                    <li class="list-group-item">
                        <div class="media"> <span class="pull-left thumb-sm"><img src="resources/images/a4.png" alt="John said" class="img-circle"></span>
                            <div class="btn-group pull-right">
                                <button type="button" class="dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-cog"></i> </button>
                                <ul class="dropdown-menu">
                                    <li><a href="#">Remove</a></li>
                                    <li><a href="#">Change Role</a></li>
                                </ul>
                            </div>
                            <div class="media-body">
                                <div><a href="#">James Bailey</a></div>
                                Approver </div>
                        </div>
                    </li>
                </ul>
            </section>
            <section class="panel panel-default portlet-item">
                <header class="panel-heading">
                    <ul class="nav nav-pills pull-right">
                        <li><div class="btn-group padder-v2"><button class="btn btn-default btn-sm"  data-toggle="modal" data-target="#tagman"><i class="fa fa-tag"></i> New</button></div></li>




                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>


                    <div class="modal fade" id="tagman" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal">&times;<span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="myModalLabel">New Tag</h4>
                                </div>
                                <div class="modal-body">


                                    <p>Choose a tag to associate with this document: </p>

                                    [Insert tree view with checkboxes]

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                                    <button type="button" class="btn btn-primary">Add Tag</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    Tags <span class="badge bg-info">8</span> </header>
                <table class="panel-body table table-striped b-light">
                    <thead>
                    <tr>

                        <th class="th-sortable" data-toggle="class">Tag <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i></span></th>
                        <th width="10">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>

                        <td>[Tag]</td>
                        <td><button type="button" class="dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-cog"></i> </button>
                            <ul class="dropdown-menu">
                                <li><a href="#">Remove</a></li>
                            </ul></td>
                    </tr>
                    </tr>

                    </tbody>
                </table>
            </section>
            <section class="panel panel-default portlet-item">
                <header class="panel-heading">
                    <ul class="nav nav-pills pull-right">
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    Version History </header>


                <table class="panel-body table table-striped b-light">
                    <thead>
                    <tr>

                        <th class="th-sortable" data-toggle="class">Version <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i></span></th>
                        <th>Date/Time</th>
                        <th>User</th>
                        <th width="10">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>

                        <td>[Version]</td>
                        <td>MM/DD/YYYY HH:MM:SS</td>
                        <td>[User]</td>
                        <td><button type="button" class="dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-cog"></i> </button>
                            <ul class="dropdown-menu">
                                <li><a href="#">Make Active</a></li>
                            </ul></td>
                    </tr>
                    </tr>

                    </tbody>
                </table>
            </section>
            <section class="panel panel-default portlet-item">
                <header class="panel-heading">
                    <ul class="nav nav-pills pull-right">
                        <li> <a href="#" class="panel-toggle text-muted"><i class="fa fa-caret-down text-active"></i><i class="fa fa-caret-up text"></i></a> </li>
                    </ul>
                    Event History </header>
                <table class="panel-body table table-striped b-light">
                    <thead>
                    <tr>

                        <th class="th-sortable" data-toggle="class">Event <span class="th-sort"> <i class="fa fa-sort-down text"></i> <i class="fa fa-sort-up text-active"></i> <i class="fa fa-sort"></i></span></th>
                        <th>Date/Time</th>
                        <th>User</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>

                        <td>[Event]</td>
                        <td>MM/DD/YYYY HH:MM:SS</td>
                        <td>[User]</td>
                    </tr>
                    </tr>

                    </tbody>
                </table>
            </section>
        </div>
    </section>
</aside>
<aside>
    <section class="vbox">
        <section class="scrollable">
            <div class="wrapper bg-empty  clearfix">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="">
                            <div class=" clearfix">
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#">AJ McClary</a></div>
                                    <small class="text-muted">Owner</small></div>
                                <div class="col-xs-3 b-r">
                                    <div class="h4 font-bold"><a href="#">MM/DD/YYYY</a></div>
                                    <small class="text-muted">Created Date</small></div>
                                <div class="col-xs-3 b-r">
                                    <div class="h4 font-bold"><a href="#">AJ McClary</a></div>
                                    <small class="text-muted">Assigned To</small></div>
                                <div class="col-xs-2 b-r">
                                    <div class="h4 font-bold"><a href="#">Evidence</a></div>
                                    <small class="text-muted">Type</small></div>
                                <div class="col-xs-2">
                                    <div class="h4 font-bold"><a href="#">ASSIGNED</a></div>
                                    <small class="text-muted">Status</small></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="wrapper ">
                <div class="row">
                    <div class="col-md-12">
                        <section class="panel b-a ">
                            <div class="panel-heading b-b bg-info">
                                <ul class="nav nav-pills pull-right">
                                    <li></li>
                                    <li> </li>
                                </ul>
                                </span> <a href="#" class="font-bold">Document</a></div>
                            <div class="panel-body">
                                <div class="complaintDetails">Brava viewer goes here...</div>
                            </div>
                        </section>
                    </div>
                </div>
                <section class="panel b-a">
                    <div class="panel-heading b-b bg-info">
                        <ul class="nav nav-pills pull-right">
                            <li style="margin-right:5px">
                                <div class="btn-group" style="margin-top:4px;">
                                    <ul class="dropdown-menu dropdown-select">
                                        <li><a href="#">
                                            <input type="radio" name="b">
                                            Filter 1</a></li>
                                        <li><a href="#">
                                            <input type="radio" name="b">
                                            Filter 2</a></li>
                                        <li><a href="#">
                                            <input type="radio" name="b">
                                            Filter 3</a></li>
                                    </ul>
                                </div>
                            </li>
                            <li>
                                <div class="btn-group padder-v2">
                                    <button class="btn btn-default btn-sm" data-toggle="tooltip" data-title="New Note"><i class="fa fa-file"></i> New</button>
                                </div>
                            </li>
                            <li>&nbsp;</li>
                        </ul>
                        <a href="#" class="font-bold">Notes</a></div>
                    <div class="panel-body no-padder">
                        <table class="table table-striped th-sortable table-hover">
                            <thead>
                            <tr>
                                <th width="68%">Note</th>
                                <th width="12%">Created</th>
                                <th width="10%">Author</th>
                                <th width="10%">Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="odd gradeA">
                                <td>[Note]</td>
                                <td>[Created]</td>
                                <td>[Author]</td>
                                <td><button type="button" class="dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-cog"></i> </button>
                                    <ul class="dropdown-menu">
                                        <li><a href="#">Delete</a></li>
                                    </ul></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
            </div>
        </section>
        <footer class="footer bg-white b-t">
            <div class="row text-center-xs">
                <div class="col-md-6 hidden-sm">
                    <p class="text-muted m-t">Showing 20-30 of 50</p>
                </div>
                <div class="col-md-6 col-sm-12 text-right text-center-xs">
                    <ul class="pagination pagination-sm m-t-sm m-b-none">
                        <li><a href="#"><i class="fa fa-chevron-left"></i></a></li>
                        <li class="active"><a href="#">1</a></li>
                        <li><a href="#">2</a></li>
                        <li><a href="#">3</a></li>
                        <li><a href="#">4</a></li>
                        <li><a href="#">5</a></li>
                        <li><a href="#"><i class="fa fa-chevron-right"></i></a></li>
                    </ul>
                </div>
            </div>
        </footer>
    </section>
</aside>
</section>
</section>
</jsp:body>
</t:detail>
