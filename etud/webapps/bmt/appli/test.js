/* Base URL of the web-service for the current user */
var wsBase = '/bmt/tata/'

//http://serveur/bmt/<User>/clean?x-http-method=post
// On teste la fonction clean pour voir si tous les bookmarks sont éffacés

async function getTag(){
        var url = wsBase + "tags"
        var x = await $.get(url);
        if (x.length == 0 ) {
                $("ul#tags").append("<li class='passed'>la liste des tags est vide</li>")
        }
        else{
                $("ul#tags").append("<li class='passed'>la liste des tags n'est pas vide</li>")
        }
}
async function test_clean(){
  var url = wsBase + "clean";
  var x = await $.post(url)
  url = wsBase + "tags";
  x = await $.get(url);
  if (x == null) {
          $("ul#tags").append("<li class='passed'>la fonction test_clean a reussi </li>")
  }
  else{
          $("ul#tags").append("<li class='failed'>la fonction test_clean a échoué </li>")
  }
}

async function test_reinit(){
        var url = wsBase + "reinit";
        var x = await $.post(url);
        url = wsBase + "tags"
        x = await $.get(url)
        if (x != null) {
                 $("ul#tags").append("<li class='passed'> La fonction test_reinit a reussi </li>")
        }
        else{
                 $("ul#tags").append("<li class='failed'> La fonction test_reinit a échoué</li>")
        }
}

async function test_add_tag(){
        //var url =
}

async function test_modify_tag(){
        var  idTag = 73 ;
        var url = wsBase + "tags/" + idTag;
        var name = "changeName"
        var tag = { "id" : idTag, "name": name};
        //var x = await $.post(url, "json=" + JSON.stringify(tag) + "&x-http-method=put")

}


$(function() {
        //test_clean();
        test_reinit();
        test_modify_tag()
})

/*
http://localhost:8080/bmt/tata/clean?x-http-method=post
*/
