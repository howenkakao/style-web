$(function(){
    $('.account-j h2').click(function(){
        $(this).parents(".account-j").find(".way").toggle();
    })

    $('li').click(function(){
        $(this).parents("ul").prev().find(".quick").html($(this).find(".quick").html())
        $(this).parents("ul").prev().find("input").val($(this).find("input").val())
    })

    $('.back').click(function(){
        $('.shade').show();
    })

    $('.first').click(function(){
        $('.shade').hide();
    })

})

