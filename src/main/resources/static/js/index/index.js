document.addEventListener('DOMContentLoaded', function() {
    var ctx = document.getElementById('usageChart').getContext('2d');
    var usageChart;

    function updateChart(region, subregion) {
        var fetchDataUrl = '/index/data?region=' + region;
        if (subregion) {
            fetchDataUrl += '&subregion=' + subregion;
        }

        fetch(fetchDataUrl)
            .then(response => response.json())
            .then(data => {
                var labels = Object.keys(data.labelsBySIGUNGU_CD).map(function(sigungu) {
                    return data.labelsBySIGUNGU_CD[sigungu];
                });

                var eleData = Object.values(data.eleUsageBySIGUNGU_CD);
                var gasData = Object.values(data.gasUsageBySIGUNGU_CD);
                console.log(eleData);
                console.log(gasData);

                if (!usageChart) {
                    usageChart = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: labels,
                            datasets: [
                                {
                                    label: '전기 사용량',
                                    data: eleData,
                                    borderColor: 'rgba(54, 162, 235, 1)',
                                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                                    borderWidth: 1
                                },
                                {
                                    label: '가스 사용량',
                                    data: gasData,
                                    borderColor: 'rgba(255, 99, 132, 1)',
                                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                                    borderWidth: 1
                                }
                            ]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'top',
                                },
                                title: {
                                    display: true,
                                    text: '서울 및 경기도 시군구별 전기 및 가스 사용량'
                                }
                            }
                        }
                    });
                } else {
                    usageChart.data.labels = labels;
                    usageChart.data.datasets[0].data = eleData;
                    usageChart.data.datasets[1].data = gasData;
                    usageChart.update();
                }
            })
            .catch(error => {
                console.error('Error:', error.message);
                showError('데이터를 가져오는 도중 오류가 발생했습니다: ' + error.message);
            });
    }

    document.getElementById('regionSelect').addEventListener('change', function(event) {
        var region = event.target.value;
        var subregionSelect = document.getElementById(region + 'SubSelect');
        var subregion = subregionSelect ? subregionSelect.value : null;
        updateChart(region, subregion);

        // 지역 선택에 따라서 시군구 선택 옵션 변경
        if (region === 'seoul') {
            document.getElementById('seoulSubSelect').style.display = 'inline-block';
            document.getElementById('gyeonggiSubSelect').style.display = 'none';
        } else if (region === 'gyeonggi') {
            document.getElementById('seoulSubSelect').style.display = 'none';
            document.getElementById('gyeonggiSubSelect').style.display = 'inline-block';
        }
    });

    document.getElementById('seoulSubSelect').addEventListener('change', function(event) {
        var subregion = event.target.value;
        updateChart('seoul', subregion);
    });

    document.getElementById('gyeonggiSubSelect').addEventListener('change', function(event) {
        var subregion = event.target.value;
        updateChart('gyeonggi', subregion);
    });

    // 기본값으로 서울 강남 지역 그래프 표시
    updateChart('seoul', 'gangnam');

    function showError(message) {
        console.error(message);
        var errorDiv = document.createElement('div');
        errorDiv.textContent = message;
        errorDiv.style.color = 'red';
        document.body.appendChild(errorDiv);
    }
});